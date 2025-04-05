package asoiafnexus.tournament.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The ongoing state management of the event.
 */
public class State {

    private int round;
    private Map<UUID, List<Result>> results;
    private List<Pairing> currentPairings;

    public State() {
        round = 1;
        results = new HashMap<>();
        currentPairings = null;
    }

    /**
     *
     * @param newPairings Participants that will be playing eachother this round
     */
    public void setPairings(List<Pairing> newPairings) {
        // Precondition: All paired participants belong to the tournament
        // Precondition: All participants are paired
        var incomingParticipants = newPairings.stream().flatMap(p -> Stream.of(p.p1(), p.p2())).collect(Collectors.toSet());
        var participantSet = results.keySet();
        if(!incomingParticipants.equals(participantSet)) {
            throw new IllegalArgumentException("Cannot set pairings unless they contain all tournament participants");
        }
        // Set new pairings
        this.currentPairings = newPairings;

        // Remove any results from this round that no longer match the new pairings
        this.currentPairings.stream().forEach(pair -> {
            results.computeIfPresent(pair.p1(), (id, rs) -> rs.stream().filter(r -> r.round() == this.round && Objects.equals(pair.p2(), r.opponent())));
        });
    }

    public boolean roundOver() {

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
        return results.stream()
                .map(Result::points)
                .reduce((x, y) -> IntStream
                        .range(0, Math.min(x.size(), y.size()))
                        .map(idx -> x.get(idx) + y.get(idx))
                        .boxed()
                        .toList())
                .orElse(Collections.emptyList());
    }
}
