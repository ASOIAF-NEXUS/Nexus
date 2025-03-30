package asoiafnexus.tournament.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface MakePairings {
    List<Pairing> makePairings(List<Player> players);

    /**
     * Creates a new set of pairings by randomizing the players and grouping
     * two at a time. Does not take pairing history into consideration.
     */
    MakePairings RandomPairings = (players) -> {
        var p = new ArrayList<>(players);
        Collections.shuffle(p);
        var newPairings = new ArrayList<Pairing>();

        for (var itor = p.iterator(); itor.hasNext(); ) {
            Player p1 = itor.next();
            Player p2 = null;
            if (itor.hasNext()) p2 = itor.next();
            newPairings.add(new Pairing(p1, p2));
        }
        return newPairings.stream().toList();
    };

    Comparator<List<Integer>> pointsComparitor = (p1, p2) -> IntStream
            .range(0, Math.min(p1.size(), p2.size()))
            .map(idx -> -1 * p1.get(idx).compareTo(p2.get(idx)))
            .filter(n -> n != 0)
            .findFirst()
            .orElse(0);

    MakePairings SortedPairings = (players) -> {
        // Sort players from top to bottom score
        var availablePlayers = players.stream().sorted(Comparator.comparing(
                Player::points,
                pointsComparitor))
                .collect(Collectors.toList());

        var newPairings = new ArrayList<Pairing>();

        // Advance throught the list of players assigning highest-scoring players with each other
        for (var itor = availablePlayers.iterator(); itor.hasNext(); itor = availablePlayers.iterator()) {
            Player p1 = itor.next(); itor.remove(); // remove P1 from the pool of available players
            Player p2 = null;
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

            newPairings.add(new Pairing(p1, p2));
        }
        return newPairings.stream().toList();
    };
}
