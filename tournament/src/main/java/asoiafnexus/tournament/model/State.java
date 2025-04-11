package asoiafnexus.tournament.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The ongoing state management of the event.
 */
public record State(
        int round,
        List<Result> results,
        List<Pairing> currentPairings
) {
    public State(List<Pairing> initialPairings) {
        this(1, Collections.emptyList(), initialPairings);
    }

    public List<Pairing> getCurrentPairings() {
        return Collections.unmodifiableList(currentPairings);
    }

    /**
     *
     * @param newPairings Participants that will be playing eachother this round
     */
    public State setPairings(List<Pairing> newPairings) {
        return new State(round, results, newPairings);
    }

    public boolean roundOver() {
        return false;
    }

    public State submit(Result result) {
        // current round?
        if (result.round() != round) {
            throw new IllegalStateException("Result must be for the current round");
        }
        // Are these players paired?
        var possiblePairing = new Pairing(result.participant(), result.opponent());
        if (currentPairings.stream().noneMatch(possiblePairing::equals)) {
            throw new IllegalStateException("Result for players that are not currently paired together");
        }
        return new State(
                round,
                Stream.concat(
                        Stream.of(result),
                        results.stream().filter(r ->
                                // remove previous result to allow a user/TO to correct a result
                                !(Objects.equals(result.participant(), r.participant()) && Objects.equals(result.opponent(), r.opponent()))))
                        .toList(),
                currentPairings);
    }

    public Set<UUID> missingResults() {
        var thisRoundsResults = results.stream()
                .filter(r -> r.round() == round)
                .map(Result::participant)
                .collect(Collectors.toSet());

        return currentPairings.stream()
                .flatMap(p -> p.asSet().stream())
                .filter(id -> !thisRoundsResults.contains(id))
                .collect(Collectors.toSet());
    }

    public State nextRound(List<Participant> participants, MakePairings pairingStrategy) {
        var missingResults = missingResults();
        if(!missingResults.isEmpty()) {
            throw new IllegalStateException("Not all results are in");
        }

        return new State(round + 1, results, pairingStrategy.makePairings(participants, results));
    }
}
