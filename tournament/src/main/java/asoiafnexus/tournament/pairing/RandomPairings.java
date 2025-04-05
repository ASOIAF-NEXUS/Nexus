package asoiafnexus.tournament.pairing;

import asoiafnexus.tournament.model.MakePairings;
import asoiafnexus.tournament.model.Pairing;
import asoiafnexus.tournament.model.Participant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Creates a new set of currentPairings by randomizing the players and grouping
 * two at a time. Does not take pairing history into consideration.
 */
public class RandomPairings implements MakePairings {

    @Override
    public List<Pairing> makePairings(List<Participant> participants) {
        var p = new ArrayList<>(participants);
        Collections.shuffle(p);
        var newPairings = new ArrayList<Pairing>();

        for (var itor = p.iterator(); itor.hasNext(); ) {
            Participant p1 = itor.next();
            Participant p2 = null;
            if (itor.hasNext()) p2 = itor.next();
            newPairings.add(new Pairing(
                    p1.username(),
                    Optional.ofNullable(p2).map(Participant::username).orElse(null)));
        }
        return newPairings.stream().toList();
    }
}
