package asoiafnexus.tournament.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class MakePairingsTest {

    @Nested
    public class SortedPairings {

        final UUID p1 = UUID.randomUUID();
        final UUID p2 = UUID.randomUUID();
        final UUID p3 = UUID.randomUUID();
        final UUID p4 = UUID.randomUUID();
        final UUID p5 = UUID.randomUUID();
        final UUID p6 = UUID.randomUUID();
        final UUID p7 = UUID.randomUUID();
        final UUID p8 = UUID.randomUUID();

        List<Participant> players = List.of(
                new Participant(p1, Collections.emptyList()),
                new Participant(p2, Collections.emptyList()),
                new Participant(p3, Collections.emptyList()),
                new Participant(p4, Collections.emptyList()),
                new Participant(p5, Collections.emptyList()),
                new Participant(p6, Collections.emptyList()),
                new Participant(p7, Collections.emptyList()),
                new Participant(p8, Collections.emptyList())
        );

        private Set<Set<UUID>> pairingsByName(List<Pairing> pairings) {
            return pairings.stream()
                    .map(x -> {
                        if(x.bye()) {
                            return Set.of(x.p1());
                        } else {
                            return Set.of(x.p1(), x.p2());
                        }
                    })
                    .collect(Collectors.toSet());
        }

        @Test
        public void noFirstRound() {
            var pairings = MakePairings.SortedPairings.makePairings(players, Collections.emptyList());

            Assertions.assertEquals(
                    Set.of(Set.of(p1, p2),
                            Set.of(p3, p4),
                            Set.of(p5, p6),
                            Set.of(p7, p8)),
                    pairingsByName(pairings));
        }

        @Test
        public void secondRound() {
            var results = List.of(
                    new Result(p1, p2, 1, List.of(3, 4, 15)),
                    new Result(p2, p1, 1, List.of(1, 0, 15)),
                    new Result(p3, p4, 1, List.of(1, 0, 15)),
                    new Result(p4, p3, 1, List.of(3, 4, 15)),
                    new Result(p5, p6, 1, List.of(2, 2, 15)),
                    new Result(p6, p5, 1, List.of(2, 2, 15)),
                    new Result(p7, p8, 1, List.of(3, 4, 15)),
                    new Result(p8, p7, 1, List.of(0, 0, 15))
            );
            var pairings = MakePairings.SortedPairings.makePairings(players, results);

            Assertions.assertEquals(
                    Set.of(
                            Set.of(p1, p4),
                            Set.of(p7, p5),
                            Set.of(p6, p2),
                            Set.of(p3, p8)
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void thirdRound() {
            var results = List.of(
                    new Result(p1, p2, 1, List.of(3, 4, 15)),
                    new Result(p1, p4, 2, List.of(3, 4, 15)),
                    new Result(p2, p1, 1, List.of(1, 0, 15)),
                    new Result(p2, p6, 2, List.of(1, 0, 15)),
                    new Result(p3, p4, 1, List.of(1, 0, 15)),
                    new Result(p3, p8, 2, List.of(3, 4, 20)),
                    new Result(p4, p3, 1, List.of(3, 4, 15)),
                    new Result(p4, p1, 2, List.of(1, 0, 0)),
                    new Result(p5, p6, 1, List.of(2, 2, 15)),
                    new Result(p5, p7, 2, List.of(3, 3, 15)),
                    new Result(p6, p5, 1, List.of(2, 2, 15)),
                    new Result(p6, p2, 2, List.of(3, 4, 15)),
                    new Result(p7, p8, 1, List.of(3, 4, 15)),
                    new Result(p7, p5, 2, List.of(1, 0, 15)),
                    new Result(p8, p7, 1, List.of(0, 0, 15)),
                    new Result(p8, p3, 2, List.of(0, 0, 0))
            );
            var pairings = MakePairings.SortedPairings.makePairings(players, results);

            Assertions.assertEquals(
                    Set.of(
                            Set.of(p1, p6),
                            Set.of(p5, p3),
                            Set.of(p7, p4),
                            Set.of(p2, p8)
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void playersCannotPlayTheSameOpponentTwice() {
            var results = List.of(
                    new Result(p1, p2, 1, List.of(3, 4, 15)),
                    new Result(p1, p4, 2, List.of(3, 4, 15)),
                    new Result(p2, p1, 1, List.of(1, 0, 15)),
                    new Result(p2, p3, 2, List.of(3, 4, 15)),
                    new Result(p3, p4, 1, List.of(1, 0, 15)),
                    new Result(p3, p4, 2, List.of(1, 0, 15)),
                    new Result(p4, p3, 1, List.of(3, 4, 15)),
                    new Result(p4, p1, 2, List.of(1, 0, 15))
            );
            var pairings = MakePairings.SortedPairings.makePairings(players, results);

            Assertions.assertEquals(
                    Set.of(
                            Set.of(p1, p3),
                            Set.of(p2, p4)
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void weDecidedToGoOneMoreRound() {
            var results = List.of(
                    new Result(p1, p2, 1, List.of(3, 4, 15)),
                    new Result(p1, p4, 2, List.of(3, 4, 15)),
                    new Result(p1, p3, 3, List.of(3, 4, 15)),
                    new Result(p2, p1, 1, List.of(1, 0, 15)),
                    new Result(p2, p3, 2, List.of(3, 4, 15)),
                    new Result(p2, p4, 3, List.of(2, 2, 15)),
                    new Result(p3, p4, 1, List.of(1, 0, 15)),
                    new Result(p3, p4, 2, List.of(1, 0, 15)),
                    new Result(p3, p1, 3, List.of(1, 0, 15)),
                    new Result(p4, p3, 1, List.of(3, 4, 15)),
                    new Result(p4, p1, 2, List.of(1, 0, 15)),
                    new Result(p4, p2, 3, List.of(2, 2, 15))
            );
            var pairings = MakePairings.SortedPairings.makePairings(players, results);

            Assertions.assertEquals(
                    Set.of(
                            Set.of(p1, p2),
                            Set.of(p3, p4)
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void byes() {
            var pairings = MakePairings.SortedPairings.makePairings(players.subList(0, 3), Collections.emptyList());

            Assertions.assertEquals(
                    Set.of(
                            Set.of(p1, p2),
                            Set.of(p3)
                    ),
                    pairingsByName(pairings));
        }
    }
}
