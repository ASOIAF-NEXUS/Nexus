package asoiafnexus.tournament.model;

import java.util.*;

/**
 * The ongoing state management of the event.
 */
public record State(
        int round,
        Map<UUID, List<Result>> results,
        List<Pairing> currentPairings
) {
    public State(List<Pairing> initialPairings) {
        this(1, new HashMap<>(), initialPairings);
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
}
