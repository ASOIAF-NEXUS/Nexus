package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ArmyList;
import asoiafnexus.listbuilder.model.ComposableEntry;
import asoiafnexus.listbuilder.model.Unit;

import java.util.*;
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
 *    Commander is an Attachment, your army must include a unit
 *    for the Commander to be attached to.
 * •  Units and/or Attachments that list CHARACTER on their
 *    Stat card are unique. Only 1 copy of each of these Units/
 *    Attachments may be included in your army (though you can
 *    have several different Characters in your army). Note that several
 *    Characters might have multiple versions (such as Jaime Lannister
 *    “Kingslayer” and Jaime Lannister “The Young Lion”). These are still
 *    the same Character for uniqueness.
 * •  When adding Attachments into your army, they must be
 *    included in a Combat Unit, and each Combat Unit may only
 *    ever have 1 Attachment. You cannot add an Attachment into
 *    your army if there is no available Unit to place it in .
 * •  Additionally, sometimes a Unit and/or Attachment may list
 *    special requirements or restrictions to be fielded. See that
 *    Unit/Attachment’s specific Stat card for more information.
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
                .flatMap(x -> x.attachments().stream())
                .filter(x -> !Objects.equals(x.role(), Unit.Role.attachment))
                .map(Unit::fullName)
                .map(s -> String.format("%s is not an attachment.", s))
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

        if(commanderCount == 0) {
            return List.of("Every army must list a commander.");
        } else if(commanderCount > 1) {
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

        if(neutralPoints > (totalPoints * .3)) {
            return List.of("Too many neutral points.");
        } else {
            return List.of();
        }
    }

    /**
     * Solo units may not take attachments
     */
    public static List<String> soloUnitsWithAttachments(ArmyList army) {
        return army.units().stream()
                .filter(x -> x.unit().isSolo() && !x.attachments().isEmpty())
                .map(x -> x.unit().fullName())
                .map(s -> String.format("%s can not have any units attached to it.", s))
                .toList();
    }

    /**
     * Some units require other units in order to be taken
     */
    public static List<String> unitRequiresNotMet(ArmyList army) {
        var combatUnits = army.units();

        return army.unitStream()
                .filter(x -> !x.requires().isEmpty())
                .filter(unit -> unit.requires().stream()
                        .noneMatch(requirement -> combatUnits.stream().anyMatch(combatUnit -> {
                            var match = combatUnit.allUnits().stream().anyMatch(requirement::isMatch);

                            //TODO this still feels clunky. Unit model might be able to be improved
                            if(match && requirement.attributes().contains(Unit.Requires.Attributes.same_combat_unit)) {
                                match = combatUnit.allUnits().contains(unit);
                            }
                            return match;
                        })))
                .map(unit -> String.format("None of the unit requirements for %s are present.", unit.fullName()))
                .toList();
    }
}
