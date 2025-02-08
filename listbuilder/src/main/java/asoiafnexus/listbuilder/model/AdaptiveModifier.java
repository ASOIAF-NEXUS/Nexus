package asoiafnexus.listbuilder.model;

import asoiafnexus.listbuilder.validation.EntryMatcher;

import java.util.List;
import java.util.stream.Stream;

public record AdaptiveModifier(EntryMatcher entry) {

    /**
     * For every unit that matches `whenPresent` will be updated
     * to have one of the attached units points reduced by 1
     *
     * @param list
     * @return
     */
    public List<ListEntry> apply(List<ListEntry> list) {
        return list.stream().
                map(e -> {
                    if (e instanceof ComposableEntry x && entry.match(x.unit())) {
                        return updateUnit(x);
                    }
                    return e;
                })
                .toList();
    }

    /**
     * A grouping that should be updated for adaptive
     * Find an entity that is not the original unit, and an attachment that is not 0 points
     * create a copy of that AtomicEntity with all the same values but 1 point less
     */
    public ComposableEntry updateUnit(ComposableEntry entry) {
        var maybeAttachment = entry.attachments().stream()
                .filter(x -> x.points() > 0)
                .findFirst();

        if(maybeAttachment.isEmpty()) {
            return entry;
        }

        var discountedAttachment = maybeAttachment.get();
        var updatedAttachments = Stream.concat(
                Stream.of(new AtomicEntry(
                        discountedAttachment.name(),
                        discountedAttachment.title(),
                        discountedAttachment.points() - 1)),
                entry.attachments().stream().filter(discountedAttachment::equals)).toList();
        return new ComposableEntry(entry.unit(), updatedAttachments);
    }
}
