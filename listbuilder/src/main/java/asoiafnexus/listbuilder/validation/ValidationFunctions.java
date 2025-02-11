package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ArmyList;
import asoiafnexus.listbuilder.model.Faction;
import asoiafnexus.listbuilder.model.Unit;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static asoiafnexus.listbuilder.model.Faction.*;

public class ValidationFunctions {

    /**
     * An army must be composed of the primary faction, and no more than 30% of
     * the points may be taken from the neutral faction.
     */
    public static List<String> invalidUnitFaction(ArmyList army) {
        var unitsByFaction = army.unitsByFaction();
        var totalPoints = army.points();

        var wrongFactionUnits = unitsByFaction.entrySet().stream()
                .filter(x -> !Set.of(army.faction(), neutral).contains(x.getKey()))
                .flatMap(x -> x.getValue().stream().map(Unit::name))
                .map(s -> String.format("%s can not be included in a %s army list.", s, army.faction()));

        var neutralPoints = unitsByFaction.getOrDefault(neutral, Collections.emptyList())
                .stream()
                .map(Unit::points)
                .reduce(0, Integer::sum);

        // not allowed to have > 30%
        if(neutralPoints > (totalPoints * 0.3)) {
            return Stream.concat(Stream.of("Only 30% of the army list can be from the neutral faction."),
                    wrongFactionUnits).toList();
        } else {
            return wrongFactionUnits.toList();
        }

    }

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
}
