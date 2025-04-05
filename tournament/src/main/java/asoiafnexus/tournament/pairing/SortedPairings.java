package asoiafnexus.tournament.pairing;

import asoiafnexus.tournament.model.MakePairings;
import asoiafnexus.tournament.model.Pairing;
import asoiafnexus.tournament.model.Participant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SortedPairings implements MakePairings {

    public int comparePoints(List<Integer> p1, List<Integer> p2) {
        return IntStream
                .range(0, Math.min(p1.size(), p2.size()))
                .map(idx -> -1 * p1.get(idx).compareTo(p2.get(idx)))
                .filter(n -> n != 0)
                .findFirst()
                .orElse(0);
    }

    @Override
    public List<Pairing> makePairings(List<Participant> participants) {
        // Sort players from top to bottom score
        var availablePlayers = participants.stream().sorted(Comparator.comparing(
                        Participant::points,
                        this::comparePoints))
                .collect(Collectors.toList());

        var newPairings = new ArrayList<Pairing>();

        // Advance throught the list of players assigning highest-scoring players with each other
        for (var itor = availablePlayers.iterator(); itor.hasNext(); itor = availablePlayers.iterator()) {
            Participant p1 = itor.next(); itor.remove(); // remove P1 from the pool of available players
            Participant p2 = null;
            // P1 may have already played P2, pair them with the next-highest ranked player in the event
            for(var jtor = availablePlayers.iterator(); jtor.hasNext() && p2 == null; ) {
                var candidate = jtor.next();
                if(!p1.opponents().contains(candidate.username())) {
                    p2 = candidate;
                    jtor.remove(); // remove P2 from the pool of available players
                }
            }

            // Check to see if all pairs have already been played
            if(p2 == null && itor.hasNext()) {
                p2 = itor.next(); itor.remove();
            }

            newPairings.add(new Pairing(
                    p1.username(),
                    Optional.ofNullable(p2).map(Participant::username).orElse(null)));
        }
        return newPairings.stream().toList();
    }
}
