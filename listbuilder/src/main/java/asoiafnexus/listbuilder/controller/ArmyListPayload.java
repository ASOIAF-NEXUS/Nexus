package asoiafnexus.listbuilder.controller;

import asoiafnexus.listbuilder.Units;
import asoiafnexus.listbuilder.model.ComposableEntry;
import asoiafnexus.listbuilder.model.ListEntry;
import asoiafnexus.listbuilder.model.Unit;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    ) {
        /**
         * Convert a single combat unit with applied attachments into a {@link ComposableEntry}
         * by looking up all ids.
         * @param unitLookup A mapping of unit id -> {@link Unit} definition
         * @return A {@link ComposableEntry} of
         */
        public ListEntry toListEntry(Map<String, Unit> unitLookup) {
            var unit = unitLookup.get(this.unit);
            var attachments = this.attachments.stream().map(unitLookup::get).toList();
            return new ComposableEntry(unit, attachments);
        }
    }

    /**
     * Convert the entire army list into a list of {@link ListEntry}. This will apply all the
     * current balance data and validation metadata.
     * @param unitLookup A mapping of unit id -> {@link Unit} definition
     * @return The submitted army list, enriched with the current balance data
     */
    public List<ListEntry> toListEntries(Map<String, Unit> unitLookup) {
        return Stream.concat(
                        this.combatUnits.stream().map(c -> c.toListEntry(unitLookup)),
                        this.ncus.stream().map(ncu -> new ComposableEntry(unitLookup.get(ncu), List.of())))
                .toList();
    }

    /**
     * Goes over all ids in the ArmyListPayload and finds ids which are not defined by the system
     * Useful for providing 400 level responses to callers
     *
     * @param unitLookup A mapping of unit id -> Unit definition
     * @return
     */
    public List<String> invalidIds(Map<String, Unit> unitLookup) {
        return Stream.concat(
                        this.combatUnits.stream()
                                .flatMap(c -> Stream.concat(Stream.of(c.unit), c.attachments.stream())),
                        this.ncus.stream())
                .filter(id -> !unitLookup.containsKey(id))
                .toList();

    }
}
