package asoiafnexus.listbuilder.model;

import java.util.List;
import java.util.Map;

public record ValidatorDefinition(
        String id,
        String name,
        List<String> seasons,
        Map<String, RuleDefinition> general,
        Map<String, RuleDefinition> faction,
        Map<String, RuleDefinition> unit
) {
    public record RuleDefinition(
            String name,
            String description,
            String input,
            String array_type,
            String error_text,
            Boolean allow_override
    ) { }

    public static ValidatorDefinition of(String name) {
        return new ValidatorDefinition(null, name, null, null, null, null);
    }
}
