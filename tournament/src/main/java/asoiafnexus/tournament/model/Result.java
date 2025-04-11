package asoiafnexus.tournament.model;

import java.util.List;
import java.util.UUID;

public record Result(
        UUID participant,
        UUID opponent,
        int round,
        List<Integer> points
) {
}
