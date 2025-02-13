package asoiafnexus.listbuilder.model;

import java.util.Objects;

public interface Named {
    String name();
    String title();

    default String fullName() {
            if(Objects.nonNull(title())) {
                return name() + ", " + title();
            } else {
                return name();
            }
    }

    default boolean isMatch(Named named) {
        var match = true;
        if(Objects.nonNull(name())) match &= Objects.equals(name(), named.name());
        if(Objects.nonNull(title())) match &= Objects.equals(title(), named.title());
        return match;
    }
}
