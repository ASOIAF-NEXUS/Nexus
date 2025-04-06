package asoiafnexus.tournament.pairing;

import asoiafnexus.tournament.model.MakePairings;
import asoiafnexus.tournament.model.Pairing;
import asoiafnexus.tournament.model.Participant;
import asoiafnexus.tournament.model.Result;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates a new set of currentPairings by randomizing the players and grouping
 * two at a time. Does not take pairing history into consideration.
 */
public class RandomPairings implements MakePairings {

    @Override
    public List<Pairing> makePairings(List<Participant> participants, List<Result> results) {
        var p = participants.stream().map(Participant::id).collect(Collectors.toList());
        Collections.shuffle(p);
        var newPairings = new ArrayList<Pairing>();

        for (var itor = p.iterator(); itor.hasNext(); ) {
            UUID p1 = itor.next();
            UUID p2 = null;
            if (itor.hasNext()) p2 = itor.next();
            newPairings.add(new Pairing(p1, p2));
        }
        return newPairings.stream().toList();
    }
}
