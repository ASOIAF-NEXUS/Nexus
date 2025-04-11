package asoiafnexus.tournament.pairing;

import asoiafnexus.tournament.controller.TournamentController;
import asoiafnexus.tournament.model.MakePairings;
import asoiafnexus.tournament.model.Pairing;
import asoiafnexus.tournament.model.Participant;
import asoiafnexus.tournament.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates a new set of currentPairings by randomizing the players and grouping
 * two at a time. Does not take pairing history into consideration.
 */
public class RandomPairings implements MakePairings {
    private static final Logger LOG = LoggerFactory.getLogger(RandomPairings.class);

    @Override
    public String name() {
        return "Random Pairings";
    }

    @Override
    public List<Pairing> makePairings(List<Participant> participants, List<Result> results) {
        var p = participants.stream().map(Participant::id).collect(Collectors.toList());
        Collections.shuffle(p);
        var newPairings = new ArrayList<Pairing>();

        for (var idx = 0; idx < p.size(); idx += 2) {
            UUID p1 = p.get(idx);
            UUID p2 = null;
            if (idx + 1 < p.size()) {
                p2 = p.get(idx + 1);
            }

            newPairings.add(new Pairing(p1, p2));
        }
        return Collections.unmodifiableList(newPairings);
    }
}
