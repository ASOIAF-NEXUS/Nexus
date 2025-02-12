package asoiafnexus.listbuilder.model;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;
import java.util.Objects;
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
        List<Requires> requires

        ) {

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
    ) { }

    public record Requires(
            String name,
            String title,
            Set<Attributes> attributes
    ) {
        public enum Attributes {
            ignores_attachment_limits, same_combat_unit
        }

        public boolean isMatch(Unit unit) {
            var match = true;
            if(Objects.nonNull(name)) match &= Objects.equals(name, unit.name);
            if(Objects.nonNull(title)) match &= Objects.equals(title, unit.title);
            return match;
        }
    }

    public boolean adaptiveCandidate() {
        return points > 0 && role == Role.attachment;
    }

    public String fullName() {
        if(Objects.nonNull(title)) {
            return name + ", " + title;
        } else {
            return name;
        }
    }

    public Unit applyAdaptive() {
        if(points <= 0) throw new IllegalArgumentException(String.format("Tried to apply adaptive to a unit '%s' without points", id));
        if(role != Role.attachment) throw new IllegalArgumentException("Adaptive can only discount attachments");
        return new Unit(id, faction, name, title, includes, role, type, points - 1, attributes, excludes, requires);
    }

    public boolean isSolo() {
        return attributes().contains(Attributes.solo);
    }
}
