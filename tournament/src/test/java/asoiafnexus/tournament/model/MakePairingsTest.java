package asoiafnexus.tournament.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class MakePairingsTest {

    @Nested
    public class SortedPairings {

        private Set<Set<String>> pairingsByName(List<Pairing> pairings) {
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
            var players = List.of(
                    new Participant("p1", List.of(), Collections.emptyList()),
                    new Participant("p2", List.of(), Collections.emptyList()),
                    new Participant("p3", List.of(), Collections.emptyList()),
                    new Participant("p4", List.of(), Collections.emptyList()),
                    new Participant("p5", List.of(), Collections.emptyList()),
                    new Participant("p6", List.of(), Collections.emptyList()),
                    new Participant("p7", List.of(), Collections.emptyList()),
                    new Participant("p8", List.of(), Collections.emptyList())
            );
            var pairings = MakePairings.SortedPairings.makePairings(players);

            Assertions.assertEquals(
                    Set.of(Set.of("p1", "p2"),
                            Set.of("p3", "p4"),
                            Set.of("p5", "p6"),
                            Set.of("p7", "p8")),
                    pairingsByName(pairings));
        }

        @Test
        public void secondRound() {
            var players = List.of(
                    new Participant("p1", List.of(new Participant.Result(0, "p2", List.of(3, 4, 15))), Collections.emptyList()),
                    new Participant("p2", List.of(new Participant.Result(0, "p1", List.of(1, 0, 15))), Collections.emptyList()),
                    new Participant("p3", List.of(new Participant.Result(0, "p4", List.of(1, 0, 15))), Collections.emptyList()),
                    new Participant("p4", List.of(new Participant.Result(0, "p3", List.of(3, 4, 15))), Collections.emptyList()),
                    new Participant("p5", List.of(new Participant.Result(0, "p6", List.of(2, 2, 15))), Collections.emptyList()),
                    new Participant("p6", List.of(new Participant.Result(0, "p5", List.of(2, 2, 15))), Collections.emptyList()),
                    new Participant("p7", List.of(new Participant.Result(0, "p8", List.of(3, 4, 15))), Collections.emptyList()),
                    new Participant("p8", List.of(new Participant.Result(0, "p7", List.of(0, 0, 15))), Collections.emptyList())
            );
            var pairings = MakePairings.SortedPairings.makePairings(players);

            Assertions.assertEquals(
                    Set.of(
                            Set.of("p1", "p4"),
                            Set.of("p7", "p5"),
                            Set.of("p6", "p2"),
                            Set.of("p3", "p8")
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void thirdRound() {
            var players = List.of(
                    new Participant("p1", List.of(
                            new Participant.Result(0, "p2", List.of(3, 4, 15)),
                            new Participant.Result(1, "p4", List.of(3, 4, 15))
                    ), Collections.emptyList()),
                    new Participant("p2", List.of(
                            new Participant.Result(0, "p1", List.of(1, 0, 15)),
                            new Participant.Result(1, "p6", List.of(1, 0, 15))
                    ), Collections.emptyList()),
                    new Participant("p3", List.of(
                            new Participant.Result(0, "p4", List.of(1, 0, 15)),
                            new Participant.Result(1, "p8", List.of(3, 4, 20))
                    ), Collections.emptyList()),
                    new Participant("p4", List.of(
                            new Participant.Result(0, "p3", List.of(3, 4, 15)),
                            new Participant.Result(1, "p1", List.of(1, 0, 0))
                    ), Collections.emptyList()),
                    new Participant("p5", List.of(
                            new Participant.Result(0, "p6", List.of(2, 2, 15)),
                            new Participant.Result(1, "p7", List.of(3, 3, 15))
                    ), Collections.emptyList()),
                    new Participant("p6", List.of(
                            new Participant.Result(0, "p5", List.of(2, 2, 15)),
                            new Participant.Result(1, "p2", List.of(3, 4, 15))
                    ), Collections.emptyList()),
                    new Participant("p7", List.of(
                            new Participant.Result(0, "p8", List.of(3, 4, 15)),
                            new Participant.Result(1, "p5", List.of(1, 0, 15))
                    ), Collections.emptyList()),
                    new Participant("p8", List.of(
                            new Participant.Result(0, "p7", List.of(0, 0, 15)),
                            new Participant.Result(1, "p3", List.of(0, 0, 0))
                    ), Collections.emptyList())
            );
            var pairings = MakePairings.SortedPairings.makePairings(players);

            Assertions.assertEquals(
                    Set.of(
                            Set.of("p1", "p6"),
                            Set.of("p5", "p3"),
                            Set.of("p7", "p4"),
                            Set.of("p2", "p8")
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void playersCannotPlayTheSameOpponentTwice() {
            var players = List.of(
                    new Participant("p1", List.of(
                            new Participant.Result(0, "p2", List.of(3, 4, 15)),
                            new Participant.Result(1, "p4", List.of(3, 4, 15))
                    ), Collections.emptyList()),
                    new Participant("p2", List.of(
                            new Participant.Result(0, "p1", List.of(1, 0, 15)),
                            new Participant.Result(1, "p3", List.of(3, 4, 15))
                    ), Collections.emptyList()),
                    new Participant("p3", List.of(
                            new Participant.Result(0, "p4", List.of(1, 0, 15)),
                            new Participant.Result(1, "p4", List.of(1, 0, 15))
                    ), Collections.emptyList()),
                    new Participant("p4", List.of(
                            new Participant.Result(0, "p3", List.of(3, 4, 15)),
                            new Participant.Result(1, "p1", List.of(1, 0, 15))
                    ), Collections.emptyList())
            );
            var pairings = MakePairings.SortedPairings.makePairings(players);

            Assertions.assertEquals(
                    Set.of(
                            Set.of("p1", "p3"),
                            Set.of("p2", "p4")
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void weDecidedToGoOneMoreRound() {
            var players = List.of(
                    new Participant("p1", List.of(
                            new Participant.Result(0, "p2", List.of(3, 4, 15)),
                            new Participant.Result(0, "p4", List.of(3, 4, 15)),
                            new Participant.Result(0, "p3", List.of(3, 4, 15))
                    ), Collections.emptyList()),
                    new Participant("p2", List.of(
                            new Participant.Result(0, "p1", List.of(1, 0, 15)),
                            new Participant.Result(0, "p3", List.of(3, 4, 15)),
                            new Participant.Result(0, "p4", List.of(2, 2, 15))
                    ), Collections.emptyList()),
                    new Participant("p3", List.of(
                            new Participant.Result(0, "p4", List.of(1, 0, 15)),
                            new Participant.Result(0, "p4", List.of(1, 0, 15)),
                            new Participant.Result(0, "p1", List.of(1, 0, 15))
                    ), Collections.emptyList()),
                    new Participant("p4", List.of(
                            new Participant.Result(0, "p3", List.of(3, 4, 15)),
                            new Participant.Result(0, "p1", List.of(1, 0, 15)),
                            new Participant.Result(0, "p2", List.of(2, 2, 15))
                    ), Collections.emptyList())
            );
            var pairings = MakePairings.SortedPairings.makePairings(players);

            Assertions.assertEquals(
                    Set.of(
                            Set.of("p1", "p2"),
                            Set.of("p3", "p4")
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void byes() {
            var players = List.of(
                    new Participant("p1", List.of(), Collections.emptyList()),
                    new Participant("p2", List.of(), Collections.emptyList()),
                    new Participant("p3", List.of(), Collections.emptyList())
            );
            var pairings = MakePairings.SortedPairings.makePairings(players);

            Assertions.assertEquals(
                    Set.of(
                            Set.of("p1", "p2"),
                            Set.of("p3")
                    ),
                    pairingsByName(pairings));
        }

        @Test
        public void byesCannotRepeat() {
            var players = List.of(
                    new Participant("p1", List.of(), Collections.emptyList()),
                    new Participant("p2", List.of(), Collections.emptyList()),
                    new Participant("p3", List.of(), Collections.emptyList())
            );
            var pairings = MakePairings.SortedPairings.makePairings(players);

            Assertions.assertEquals(
                    Set.of(
                            Set.of("p1", "p2"),
                            Set.of("p3")
                    ),
                    pairingsByName(pairings));
        }
    }
}
