package asoiafnexus.tournament.model;

import java.util.UUID;

public record Pairing(
        UUID p1,
        UUID p2
) {
    public boolean bye() { return p2 == null; }
}
