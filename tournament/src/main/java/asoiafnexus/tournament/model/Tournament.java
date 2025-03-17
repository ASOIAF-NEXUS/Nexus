package asoiafnexus.tournament.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record Tournament(
        UUID id,
        String name,
        String description,
        String location,
        ZonedDateTime datetime,
        List<String> players
) {
}
