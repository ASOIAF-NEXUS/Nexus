package asoiafnexus.listbuilder.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public record ArmyList (
        Faction faction,
        List<ComposableEntry> units
){
    public Stream<Unit> unitStream() {
        return units.stream()
                .flatMap(u -> u.allUnits().stream());
    }

    public int points() {
        return unitStream().map(Unit::points).reduce(0, Integer::sum);
    }

    public Map<Faction, List<Unit>> unitsByFaction() {
        return unitStream().collect(groupingBy(Unit::faction));
    }

    public Map<Faction, Integer> pointsByFaction() {
        return unitStream().collect(groupingBy(Unit::faction,
                mapping(Unit::points, reducing(0, Integer::sum))));
    }
}
