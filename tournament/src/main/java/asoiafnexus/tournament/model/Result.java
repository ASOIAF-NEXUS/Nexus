package asoiafnexus.tournament.model;

import java.util.List;
import java.util.UUID;

public record Result(
        UUID participant,
        int round,
        UUID opponent,
        List<Integer> points
) {
}
