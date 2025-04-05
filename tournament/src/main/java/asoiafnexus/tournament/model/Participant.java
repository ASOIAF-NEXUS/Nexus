package asoiafnexus.tournament.model;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record Participant(
        UUID id,
        String username,
        List<ArmyList> lists
) {

    /**
     * A tuple representing how many times this player has encountered a given opponent over the course of an event.
     */
    public record OpponentFrequencies(String username, int count) { }

    public List<OpponentFrequencies> opponentFrequencies() {
        return history.stream()
                .collect(Collectors.groupingBy(
                        Result::opponent,
                        Collectors.reducing(0, r -> 1, Integer::sum)))
                .entrySet().stream()
                .map(x -> new OpponentFrequencies(x.getKey(), x.getValue()))
                .toList();
    }

    public Set<String> opponents() {
        var opponentFrequencies = opponentFrequencies();
        var maxCount = opponentFrequencies.stream()
                .mapToInt(OpponentFrequencies::count)
                .max()
                .orElse(0);

        return opponentFrequencies.stream()
                .filter(of -> of.count == maxCount)
                .map(of -> of.username)
                .collect(Collectors.toSet());
    }

    public record ArmyList(
            List<List<String>> combatUnits,
            List<String> attachments
    ) {
    }
}
