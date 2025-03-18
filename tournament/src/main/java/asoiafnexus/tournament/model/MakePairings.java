package asoiafnexus.tournament.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface MakePairings {
    List<Pairing> makePairings(List<Player> players, List<Pairing> previousPairings);

    /**
     * Creates a new set of pairings by randomizing the players and grouping
     * two at a time. Does not take pairing history into consideration.
     */
    MakePairings RandomPairings = (p, x) -> {
        var players = new ArrayList<>(p);
        Collections.shuffle(players);
        var newPairings = new ArrayList<Pairing>();

        for(var itor = players.iterator(); itor.hasNext();) {
            Player p1 = itor.next();
            Player p2 = null;
            if(itor.hasNext()) p2 = itor.next();
            newPairings.add(new Pairing(p1, p2));
        }
        return newPairings.stream().toList();
    };
}
