package asoiafnexus.listbuilder.model;

import java.util.List;
import java.util.stream.Stream;

public record ComposableEntry(
        Unit unit,
        List<Unit> attachments
) implements ListEntry {

    @Override
    public List<Unit> allEntries() {
        return Stream
                .concat(Stream.of(unit), attachments.stream())
                .toList();
    }

    public int points() {
        return attachments.stream()
                .map(Unit::points)
                .reduce(unit.points(), Integer::sum);
    }
}
