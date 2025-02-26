package asoiafnexus.listbuilder.model;

import java.util.List;
import java.util.Map;

public record Attachment(
        String id,
        String name,
        String subname,
        Statistics statistics,
        Tactics tactics,
        Fluff fluff
) {
    record Statistics(
            String faction,
            String type,
            String cost,
            List<String> abilities,
            Boolean commander,
            Boolean character
    ) { }
    record Tactics(
            Map<String, String> cards
    ) { }
    record Fluff(
            String quote
    ) { }
}
