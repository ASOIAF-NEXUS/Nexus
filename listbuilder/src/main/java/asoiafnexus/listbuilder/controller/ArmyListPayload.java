package asoiafnexus.listbuilder.controller;

import asoiafnexus.listbuilder.Units;
import asoiafnexus.listbuilder.model.ListEntry;

import java.util.List;
import java.util.Map;

/**
 * Format for accepting Army Lists over HTTP
 * Identifiers will be used to look up the current balance data, then validation
 * rules will be applied
 */
public record ArmyListPayload(
        List<CombatUnit> combatUnits,
        List<String> ncus
) {
    public record CombatUnit(
            String unit,
            List<String> attachments
    ) {}

    public List<ListEntry> toListEntries(Map<String, Units.Unit> unitLookup) {

    }
}
