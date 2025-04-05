package asoiafnexus.tournament.model;

import java.time.ZonedDateTime;

public record Details (
        String name,
        String description,
        String location,
        ZonedDateTime datetime
){
}
