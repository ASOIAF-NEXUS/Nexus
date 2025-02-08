package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.ListEntry;

import java.util.Arrays;
import java.util.List;

public interface ValidationRule {

    /**
     * A single piece of validation logic that is executed against the army definition.
     * By running all the validators we can provide feedback on all the validation
     * errors.
     *
     * @param armyList The army being validated
     * @return A list of validation warnings
     */
    public List<String> validationMessages(List<ListEntry> armyList);

    /**
     * Function composition helper for mergering multiple validation rules into a single executable.
     * Returns the combined result of validation messages.
     */
    public static ValidationRule comp(ValidationRule... rules) {
        return armyList -> Arrays.stream(rules).flatMap(r -> r.validationMessages(armyList).stream()).toList();
    }
}
