package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ListEntry;

import java.util.Collections;
import java.util.List;

/**
 * Specify that when an entity is present, it requires another entity to be in the same Combat Unit
 */
public record ComposedTogetherRule(
        EntryMatcher whenPresent,
        EntryMatcher alsoPresent
) implements ValidationRule {
    public List<String> validationMessages(List<ListEntry> list) {
        var exception = list.stream()
                .map(ListEntry::allEntries)
                .filter(xs -> xs.stream().anyMatch(whenPresent::match))
                .filter(xs -> xs.stream().noneMatch(alsoPresent::match))
                .findAny();

        if(exception.isPresent()) {
            return List.of(String.format("%s must be in the same unit as %s.",
                    whenPresent.description(),
                    alsoPresent.description()));
        }

        return Collections.emptyList();
    }
}
