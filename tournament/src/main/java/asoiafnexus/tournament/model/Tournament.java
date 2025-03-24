package asoiafnexus.tournament.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public record Tournament(
        UUID id,
        String name,
        String description,
        String location,
        ZonedDateTime datetime,
        List<Player> players,
        List<Pairing> pairings
) {
    public boolean started() {
        return !pairings.isEmpty();
    }

    public List<Pairing> newPairings(MakePairings strategy) {
        return Stream.concat(
                pairings.stream(),
                strategy.makePairings(players, pairings).stream()
        ).toList();
    }
}
