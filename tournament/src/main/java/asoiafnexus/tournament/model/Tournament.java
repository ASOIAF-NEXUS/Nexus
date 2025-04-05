package asoiafnexus.tournament.model;

import java.util.*;

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
        if(started()) {
            throw new IllegalStateException("Tournament has already been started.");
        }
        State state = new State();
        state.setPairings(MakePairings.RandomPairings.makePairings(participants));
    }

    public boolean roundOver() {
        if(!started()) {
            throw new IllegalArgumentException("Tournament has not yet started.");
        }
        return state.roundOver();
    }

    /**
     * @param strategy Algorithm for determining new pairings
     * @return A new Tournament instance with the current pairings updated
     * @throws IllegalStateException when all participants have not submitted
     * their results.
     */
    public Tournament nextRound(MakePairings strategy) {

        return new Tournament(
                id,
                details,
                d,
                strategy.makePairings(participants.values().stream().toList()));
    }

    /**
     * @param username The username of the participant submitting their result.
     * @param result The outcome of the match the participant is reporting. The
     *               result must be for a new round, otherwise a previous result will
     *               be overwritten.
     * @return a new Tournament instance where the matching participant has been updated
     *         to include the submitted result.
     * @throws IllegalArgumentException when a precondition is not met
     */
    public Tournament submitResult(String username, Participant.Result result) {
        if(!participants.containsKey(username)) {
            throw new IllegalArgumentException("User is not a participant of this tournament.");
        }
        var participant = participants.get(username);
        if(result.round() > participant.history().size() + 1) {
            throw new IllegalArgumentException("Resulting round is ahead of the rest of the tournament");
        }
        var history = new ArrayList<>(participant.history());
        if(result.round() == history.size()) {
            history.add(result);
        } else {
            history.set(result.round() -1, result);
        }
        return new Tournament(
                id,
                details,
                ,
                currentPairings
    }
}
