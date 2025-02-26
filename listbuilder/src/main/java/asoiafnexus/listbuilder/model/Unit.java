package asoiafnexus.listbuilder.model;

import java.util.List;

public record Unit (
        String id,
        String name,
        Statistics statistics,
        Fluff fluff
){
    record Statistics(
            String version,
            String faction,
            String type,
            Integer cost,
            Integer speed,
            Integer defense,
            Integer morale,
            List<Attack> attacks,
            List<String> abilities,
            String tray
    ) {
        record Attack(
                String name,
                String type,
                Integer hit,
                List<String> dice
        ) { }
    }
    record Fluff(
            String lore
    ) { }
}
