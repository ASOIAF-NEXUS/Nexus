package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ListEntry;

import java.util.Collections;
import java.util.List;

/**
 * Some units can only be taken when other units are also included in the army
 */
public record RequiresEntitiesRule(
        EntryMatcher whenPresent,
        List<EntryMatcher> requiredEntities,
        OpType type
) implements ValidationRule {

    public enum OpType {AND, OR} // NOTE: this might always be `or`

    public List<String> validationMessages(List<ListEntry> list) {
        var allEntries = list.stream()
                .flatMap(xs -> xs.allEntries().stream())
                .toList();

        if(allEntries.stream().anyMatch(whenPresent::match)) {
            switch(type) {
                case AND:
                    return requiredEntities.stream()
                            .filter(matcher -> allEntries.stream().noneMatch(matcher::match))
                            .map(matcher -> String.format("%s must be in the same army as %s", matcher, whenPresent))
                            .toList();

                case OR:
                    if(requiredEntities.stream().noneMatch(matcher -> allEntries.stream().anyMatch(matcher::match))) {
                        return List.of(String.format("One of %s must be in the same army as %s.",
                                whenPresent.description(),
                                requiredEntities.stream().map(EntryMatcher::description).toList()));
                    }
                    break;
            }
        }

        return Collections.emptyList();
    }
}
