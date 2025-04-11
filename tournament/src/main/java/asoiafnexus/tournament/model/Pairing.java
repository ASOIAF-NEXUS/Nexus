package asoiafnexus.tournament.model;

import java.util.Set;
import java.util.UUID;

public record Pairing(
        UUID p1,
        UUID p2
) {
    public boolean bye() { return p2 == null; }

    public Set<UUID> asSet() {
        if(bye()) {
            return Set.of(p1);
        } else {
            return Set.of(p1, p2);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof Pairing p)) {
            return false;
        }

        return asSet().equals(p.asSet());
    }
}
