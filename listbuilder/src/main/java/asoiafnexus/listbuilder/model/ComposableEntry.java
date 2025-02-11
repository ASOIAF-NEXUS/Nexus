package asoiafnexus.listbuilder.model;

import java.util.List;
import java.util.stream.Stream;

public record ComposableEntry(
        Unit unit,
        List<Unit> attachments
) {

    public List<Unit> allUnits() {
        return Stream
                .concat(Stream.of(unit), attachments.stream())
                .toList();
    }
}
