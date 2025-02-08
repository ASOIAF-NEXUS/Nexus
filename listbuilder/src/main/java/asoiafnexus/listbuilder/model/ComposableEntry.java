package asoiafnexus.listbuilder.model;

import java.util.List;
import java.util.stream.Stream;

public record ComposableEntry(
        AtomicEntry unit,
        List<AtomicEntry> attachments
) implements ListEntry {

    @Override
    public List<AtomicEntry> allEntries() {
        return Stream.concat(Stream.of(unit), attachments.stream())
                .flatMap(e -> e.allEntries().stream())
                .toList();
    }

    public int points() {
        return attachments.stream()
                .map(AtomicEntry::points)
                .reduce(unit.points(), Integer::sum);
    }
}
