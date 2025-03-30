package asoiafnexus.tournament.model;

import java.util.Collections;
import java.util.List;

public record Pairing(
        Player p1,
        Player p2
) {
    public boolean bye() { return p2 == null; }
}
