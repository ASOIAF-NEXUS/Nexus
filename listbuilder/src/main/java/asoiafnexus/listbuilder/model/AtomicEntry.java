package asoiafnexus.listbuilder.model;

import java.util.List;

/**
 * Represents the building blocks of an army list. Larger concepts are created by grouping
 * these atomic entries together.w
 */
public record AtomicEntry(
        String name,
        String title,
        int points
) implements ListEntry {

    @Override
    public List<AtomicEntry> allEntries() {
        return List.of(this);
    }
}