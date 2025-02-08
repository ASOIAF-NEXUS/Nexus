package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ListEntry;

import java.util.Collections;
import java.util.List;

/**
 * Special case of {@link RequiresEntitiesRule} when only a single requirement is defined,
 * there is no need to tell the system if it is a conjunctive or disjunctive operation.
 */
public record RequiresEntryRule(
       EntryMatcher whenPresent,
       EntryMatcher requiredEntity
) implements ValidationRule {
    public List<String> validationMessages(List<ListEntry> list) {
        var entries = list.stream()
                .flatMap(e -> e.allEntries().stream())
                .toList();

        if (entries.stream().anyMatch(whenPresent::match)
                && entries.stream().noneMatch(requiredEntity::match)) {

            return List.of(String.format("List must contain %s when including %s.",
                    requiredEntity.description(),
                    whenPresent.description()));
        }
        return Collections.emptyList();
    }
}
