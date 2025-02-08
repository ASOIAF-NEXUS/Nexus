package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.AtomicEntry;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Objects.*;

/**
 * Provides matching logic to search through an Army List and apply logic to specific entries.
 * Matches based on the name & title of an entity in the Army List. Regex patterns can be used.
 * This is mainly to allow scenarios like Summer requiring Bran, but Bran is defined as "Hodor And Bran"
 */
public record EntryMatcher(
        String name,
        String title
) {
    public EntryMatcher(String name) { this(name, null); }

    public boolean match(AtomicEntry entry) {
        var isMatch = true;
        if(nonNull(name) && nonNull(entry.name())) isMatch &= Pattern.matches(name, entry.name());
        if(nonNull(title) && nonNull(entry.title())) isMatch &= Pattern.matches(title, entry.title());
        return isMatch;
    }

    public String description() {
        if(isNull(name) && isNull(title)) return "*";
        if(nonNull(name) && nonNull(title)) return name + ", " + title;
        return Optional.ofNullable(name).orElse(title);
    }
}
