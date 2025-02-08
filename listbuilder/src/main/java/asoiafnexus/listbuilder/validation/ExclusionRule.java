package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ListEntry;

import java.util.Collections;
import java.util.List;

/**
 * Requires that when an Entity is present in the Army List, certain other entities can
 * not be selected.
 *
 * Example: Qyburn NCU is not allowed to be taken with Varys NCU
 */
public record ExclusionRule(
        EntryMatcher whenPresent,
        List<EntryMatcher> excludeEntities
) implements ValidationRule {
    public List<String> validationMessages(List<ListEntry> list) {
        var entries = list.stream()
                .flatMap(e -> e.allEntries().stream())
                .toList();

        if (entries.stream().anyMatch(whenPresent::match)) {
            var exceptions = excludeEntities.stream()
                    .filter(matcher -> entries.stream().anyMatch(matcher::match))
                    .toList();

            if(!exceptions.isEmpty()) {
                return List.of(String.format("List may not contain the following entries while %s is present: %s.",
                        whenPresent.description(),
                        exceptions.stream().map(EntryMatcher::description).toList()));
            }
        }
        return Collections.emptyList();
    }
}
