package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.AtomicEntry;
import asoiafnexus.listbuilder.model.ListEntry;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An army may never have more than one of the same character defined
 * Characters can have titles, but if the entity has the same name then they are considered duplicated
 * @param characters List of character names
 */
public record CharacterRule(Set<String> characters)
implements ValidationRule {

    @Override
    public List<String> validationMessages(List<ListEntry> armyList) {
        var groupedCharacters = armyList.stream()
                .flatMap(e -> e.allEntries().stream())
                .map(AtomicEntry::name)
                .filter(characters::contains)
                .collect(Collectors.groupingBy(Function.identity()));

        return groupedCharacters.entrySet().stream()
                .filter(me -> me.getValue().size() > 1)
                .map(me -> String.format("The character %s can only be selected once", me.getKey()))
                .toList();
    }
}
