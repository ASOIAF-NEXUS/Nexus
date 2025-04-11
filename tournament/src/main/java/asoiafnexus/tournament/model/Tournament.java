package asoiafnexus.tournament.model;

import java.util.*;
import java.util.function.Function;

public record Tournament(
        UUID id,
        Details details,
        List<Participant> participants,
        MakePairings pairingsStrategy,
        State state
) {
    public boolean started() {
        return state != null;
    }

    public Tournament start() {
        return new Tournament(
                id,
                details,
                participants,
                pairingsStrategy,
                new State(MakePairings.RandomPairings.makePairings(participants, Collections.emptyList())));
    }

    public Tournament updateState(Function<State, State> updater) {
        return new Tournament(
                id,
                details,
                participants,
                pairingsStrategy,
                updater.apply(state)
        );
    }
}
