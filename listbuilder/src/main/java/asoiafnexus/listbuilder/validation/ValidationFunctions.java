package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ArmyList;
import asoiafnexus.listbuilder.model.ComposableEntry;
import asoiafnexus.listbuilder.model.Unit;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static asoiafnexus.listbuilder.model.Faction.*;
import static java.util.Collections.emptyList;

/**
 * <i>Taken from the 2021-s5 rulebook</i>
 * <h1>Add Units/Attachments</h1>
 * When adding Units and Attachments into your army, there is no
 * restriction to the number of each individual Unit and/or Attachment
 * that you may field, with the following exceptions:
 * •  Your army may only include 1 Commander. If your
 * Commander is an Attachment, your army must include a unit
 * for the Commander to be attached to.
 * •  Units and/or Attachments that list CHARACTER on their
 * Stat card are unique. Only 1 copy of each of these Units/
 * Attachments may be included in your army (though you can
 * have several different Characters in your army). Note that several
 * Characters might have multiple versions (such as Jaime Lannister
 * “Kingslayer” and Jaime Lannister “The Young Lion”). These are still
 * the same Character for uniqueness.
 * •  When adding Attachments into your army, they must be
 * included in a Combat Unit, and each Combat Unit may only
 * ever have 1 Attachment. You cannot add an Attachment into
 * your army if there is no available Unit to place it in .
 * •  Additionally, sometimes a Unit and/or Attachment may list
 * special requirements or restrictions to be fielded. See that
 * Unit/Attachment’s specific Stat card for more information.
 */
public class ValidationFunctions {

    /**
     * Requires that every unit entry in the army list is filled out
     */
    public static List<String> noNullCombatUnits(ArmyList army) {
        return army.units().stream()
                .filter(x -> Objects.isNull(x.unit()))
                .findAny()
                .map(_ -> List.of("All entries must have a unit."))
                .orElse(emptyList());
    }

    /**
     * Everything other than an attachment is a unit
     */
    public static List<String> invalidCombatUnit(ArmyList army) {
        return army.units().stream()
                .map(ComposableEntry::unit)
                .filter(x -> Objects.equals(x.role(), Unit.Role.attachment))
                .map(Unit::fullName)
                .map(s -> String.format("%s can not be listed as a unit", s))
                .toList();
    }

    /**
     * Only attachment types can be listed under attachment
     */
    public static List<String> invalidAttachment(ArmyList army) {
        return army.units().stream()
                .flatMap(x -> x.attachments().stream().filter(y -> !Objects.equals(y.role(), Unit.Role.attachment)))
                .map(Unit::fullName)
                .map(s -> String.format("%s is not an attachment.", s))
                .toList();
    }

    /**
     * Only attachment types can be listed under attachment
     */
    public static List<String> attachmentTypeDiffers(ArmyList army) {
        return army.units().stream()
                .flatMap(x -> x.attachments().stream().filter(y -> !Objects.equals(x.unit().type(), y.type())))
                .map(Unit::fullName)
                .map(s -> String.format("%s cannot be attached to its combat unit.", s))
                .toList();
    }

    /**
     * Attachments can only be specified for combat units
     */
    public static List<String> attachmentsOnNonCombatUnits(ArmyList army) {
        return army.units().stream()
                .filter(x -> !x.attachments().isEmpty())
                .map(ComposableEntry::unit)
                .filter(unit -> Objects.equals(unit.role(), Unit.Role.ncu))
                .map(Unit::fullName)
                .map(s -> String.format("Attachments can not be listed for %s", s))
                .toList();
    }

    /**
     * Every army must have exactly one commander listed.
     */
    public static List<String> missingCommander(ArmyList army) {
        var commanderCount = army.unitStream()
                .filter(x -> x.attributes().contains(Unit.Attributes.commander))
                .count();

        if (commanderCount == 0) {
            return List.of("Every army must list a commander.");
        } else if (commanderCount > 1) {
            return List.of("The army may only contain one commander.");
        } else {
            return List.of();
        }
    }

    /**
     * Finds all occurrences in the army where a character is taken more than once.
     */
    public static List<String> duplicateCharacters(ArmyList army) {
        var unitsByName = army.units().stream()
                .flatMap(u -> u.allUnits().stream())
                .filter(u -> u.attributes().contains(Unit.Attributes.character))
                .collect(Collectors.groupingBy(Unit::name));

        return unitsByName.entrySet().stream()
                .filter(x -> x.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .map(s -> String.format("Character %s may only appear in the army once.", s))
                .toList();
    }

    /**
     * An army may only be composed of units of the chosen faction or the neutral faction.
     */
    public static List<String> invalidUnitFaction(ArmyList army) {
        var validFactions = Set.of(army.faction(), neutral);
        return army.unitStream()
                .filter(x -> !validFactions.contains(x.faction()))
                .map(x -> String.format("%s cannot be taken in a %s army.", x.fullName(), army.faction()))
                .toList();
    }

    /**
     * An army may not exceed 30% of its points in neutral units.
     */
    public static List<String> tooManyNeutralUnits(ArmyList army) {
        var totalPoints = army.points();
        var neutralPoints = army.unitStream()
                .filter(x -> Objects.equals(x.faction(), neutral))
                .map(Unit::points)
                .reduce(0, Integer::sum);

        if (neutralPoints > (totalPoints * .3)) {
            return List.of("Too many neutral points.");
        } else {
            return List.of();
        }
    }

    public static boolean shouldValidateSoloUnit(Unit unit) {
        return unit.attributes().contains(Unit.Attributes.solo);
    }

    /**
     * Builds a validator for a unit with the solo attribute
     *
     * @param unit   The solo unit
     * @param parent The combat unit it is recorded in
     * @return A validator that asserts the unit's constraint
     */
    public static Function<ArmyList, List<String>> validateSoloUnit(Unit unit, ComposableEntry parent) {
        return _ -> {
            if (parent.unit() == unit && parent.attachments().isEmpty()) {
                return emptyList();
            } else {
                return List.of(String.format("%s is a Solo unit and may not have any attachments", unit.fullName()));
            }
        };
    }

    public static boolean shouldCheckUnitRequires(Unit unit) {
        return !unit.requires().isEmpty();
    }

    /**
     * Builds a validator for a unit that requires other entries in the army list
     *
     * @param unit   The Unit with requirement validation
     * @param parent The combat unit it is recorded in
     * @return A validator that asserts the unit's constraint
     */
    public static Function<ArmyList, List<String>> unitRequiresNotMet(Unit unit, ComposableEntry parent) {
        return army -> unit.requires().stream()
                .filter(req -> {
                    var units = req.attributes().contains(Unit.Requires.Attributes.same_combat_unit)
                            ? parent.allUnits().stream()
                            : army.unitStream();
                    return units.anyMatch(req::isMatch);
                })
                .findAny()
                .map(_ -> List.<String>of())
                .orElse(List.of(String.format("Required units for %s not present.", unit.fullName())));
    }

    public static boolean shouldCheckUnitExcludes(Unit unit) {
        return !unit.excludes().isEmpty();
    }

    /**
     * Builds a validator for a unit that fails whenever another specified unit is present
     *
     * @param unit   The Unit with requirement validation
     * @param parent The combat unit it is recorded in
     * @return A validator that asserts the unit's constraint
     */
    public static Function<ArmyList, List<String>> unitExcludesNotMet(Unit unit, ComposableEntry parent) {
        return army -> unit.excludes().stream()
                .filter(exc -> {
                    var units = army.unitStream();
                    return units.anyMatch(exc::isMatch);
                })
                .map(exc -> String.format("Cannot field %s when taking %s", exc.fullName(), unit.fullName()))
                .toList();
    }

    private static final Map<Function<Unit, Boolean>, BiFunction<Unit, ComposableEntry, Function<ArmyList, List<String>>>>
            unitSpecificValidators = Map.of(
            ValidationFunctions::shouldValidateSoloUnit, ValidationFunctions::validateSoloUnit,
            ValidationFunctions::shouldCheckUnitExcludes, ValidationFunctions::unitExcludesNotMet,
            ValidationFunctions::shouldCheckUnitRequires, ValidationFunctions::unitRequiresNotMet
    );

    public static List<String> unitSpecificValidators(ArmyList army) {
        return army.units().stream().flatMap(x ->
                x.allUnits().stream().flatMap(y ->
                unitSpecificValidators.entrySet().stream().filter(e -> e.getKey().apply(y)).map(e -> e.getValue().apply(y, x))))
                .flatMap(v -> v.apply(army).stream())
                .toList();
    }

    public static List<String> multipleLoyalties(ArmyList army) {
        var loyalties = army.unitStream()
                .filter(unit -> Objects.nonNull(unit.loyalty()))
                .collect(Collectors.groupingBy(Unit::loyalty, Collectors.counting()));
        if(loyalties.size() > 1) {
            return List.of("An army cannot contain units of different loyalties.");
        } else {
            return List.of();
        }
    }
}
