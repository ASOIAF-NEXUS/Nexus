package asoiafnexus.tournament.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Participant(
        UUID id,
        List<ArmyList> lists
) {

    public record ArmyList(
            List<List<String>> combatUnits,
            List<String> attachments
    ) { }

    public Set<UUID> opponents(List<Result> results) {
        return results.stream()
                .filter(r -> Objects.equals(id, r.participant()))
                .map(Result::opponent)
                .collect(Collectors.toSet());
    }

    public List<Integer> calculateTournamentPoints(List<Result> results) {
        return results.stream()
                // Get results relating to this participant
                .filter(r -> Objects.equals(id, r.participant()))
                .map(Result::points)
                // Sum the tournament points at each index
                .reduce((x, y) -> IntStream
                        .range(0, Math.min(x.size(), y.size()))
                        .map(idx -> x.get(idx) + y.get(idx))
                        .boxed()
                        .toList())
                .orElse(Collections.emptyList());
    }
}
