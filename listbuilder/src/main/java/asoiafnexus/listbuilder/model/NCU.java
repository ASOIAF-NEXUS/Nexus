package asoiafnexus.listbuilder.model;

import java.util.List;

public record NCU(
        String id,
        String name,
        String subname,
        Statistics statistics,
        Fluff fluff
) {
    record Statistics(
            String version,
            String faction,
            Integer cost,
            List<Ability> abilities,
            Tokens tokens
    ) {
        record Ability(
                String name,
                List<String> effect
        ) { }
        record Tokens(
                Integer number,
                String name
        ) { }

    }
    record Fluff(
            String quote
    ) { }
}
