package asoiafnexus.tournament.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record Player(
        String username,
        List<Result> history,
        List<ArmyList> lists
) {
    public Player(String username) {
        this(username, Collections.emptyList(), Collections.emptyList());
    }

    public record Result(
            int round,
            String opponent,
            List<Integer> points
    ) {
    }

    /**
     * Calculates the players total points earned for the purpose of sorting their placement
     * Example:
     *     Round 1 [3TP 4SP 15UD]
     *     Round 2 [2TP 2SP 12UD]
     *     Current [5TP 6SP 27UD]
     * @return the sum of the players Primary and Secondary points
     */
    public List<Integer> points() {
        return history.stream()
                .map(Result::points)
                .reduce((x, y) -> IntStream
                        .range(0, Math.min(x.size(), y.size()))
                        .map(idx -> x.get(idx) + y.get(idx))
                        .boxed()
                        .toList())
                .orElse(Collections.emptyList());
    }

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
