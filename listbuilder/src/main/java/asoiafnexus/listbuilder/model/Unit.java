package asoiafnexus.listbuilder.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record Unit(
        String id,
        Faction faction,
        String name,
        String title,
        String includes,
        Role role,
        Type type,
        int points,
        Set<Attributes> attributes,
        List<Excludes> excludes,
        List<Requires> requires,
        String loyalty
        ) implements Named {

    @Override
    public Set<Attributes> attributes() {
        return Optional.ofNullable(attributes).orElse(Set.of());
    }

    @Override
    public List<Excludes> excludes() {
        return Optional.ofNullable(excludes).orElse(List.of());
    }

    @Override
    public List<Requires> requires() {
        return Optional.ofNullable(requires).orElse(List.of());
    }

    public enum Role {
        combat_unit, attachment, enemy_attachment, ncu
    }

    public enum Type {
        infantry, cavalry, monster, war_machine, ncu
    }

    public enum Attributes {
        adaptive, character, commander, solo
    }

    public record Excludes(
            String name,
            String title
    ) implements Named { }

    public record Requires(
            String name,
            String title,
            Set<Attributes> attributes
    ) implements Named {
        public Set<Attributes> attributes() { return Optional.ofNullable(attributes).orElse(Collections.emptySet()); }

        public enum Attributes {
            ignores_attachment_limits, same_combat_unit
        }
    }

    public boolean adaptiveCandidate() {
        return points > 0 && role == Role.attachment;
    }

    public Unit applyAdaptive() {
        if(points <= 0) throw new IllegalArgumentException(String.format("Tried to apply adaptive to a unit '%s' without points", id));
        if(role != Role.attachment) throw new IllegalArgumentException("Adaptive can only discount attachments");
        return new Unit(id, faction, name, title, includes, role, type, points - 1, attributes, excludes, requires, loyalty);
    }

    public boolean isSolo() {
        return attributes().contains(Attributes.solo);
    }
}
