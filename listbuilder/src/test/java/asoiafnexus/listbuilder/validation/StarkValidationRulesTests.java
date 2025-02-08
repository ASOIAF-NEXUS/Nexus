package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.model.AtomicEntry;
import asoiafnexus.listbuilder.model.ComposableEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;

public class StarkValidationRulesTests {

    // shared entry definitions
    AtomicEntry rob = new AtomicEntry("Rob Stark", "King In The North", 4);
    AtomicEntry eddardNCU = new AtomicEntry("Eddard Stark", "Hand Of The King", 5);
    AtomicEntry eddardCommander = new AtomicEntry("Eddard Stark", "Lord Of Winterfell", 0);
    AtomicEntry honorGuard = new AtomicEntry("Eddard's Honor Guard", null, 6);
    AtomicEntry jaqenAttach = new AtomicEntry("Jaqen H'Ghar", "Mysterious Prisoner", 1);
    AtomicEntry aryaNcu = new AtomicEntry("Arya Stark", "The Wolf Girl", 4);
    AtomicEntry aryaAttach = new AtomicEntry("Arya Stark", "Fugitive Princess", 1);
    AtomicEntry swornSword = new AtomicEntry("Stark Sworn Swords", null, 5);
    AtomicEntry rickon = new AtomicEntry("Rickon Stark", "Prince Of Winterfell", 1);
    AtomicEntry osha = new AtomicEntry("Osha", "Spearwife Guardian", 0);

    ValidationRule rule = new StarkFactionValidation().STARK_FACTION_VALIDATION;

    @Test
    public void noErrorsWhenNothingToValidate() {
        Assertions.assertEquals(emptyList(), rule.validationMessages(emptyList()));
    }

    @Test
    public void robStarkKingInTheNorth() {
        var attachedHonorGuard = new ComposableEntry(honorGuard, List.of(eddardCommander));

        Assertions.assertEquals(emptyList(), rule.validationMessages(List.of(rob)));

        Assertions.assertEquals(List.of("List may not contain the following entries while Rob Stark, King In The North is present: [Eddard Stark]."),
                rule.validationMessages(List.of(rob, eddardNCU)));
        Assertions.assertEquals(List.of("List may not contain the following entries while Rob Stark, King In The North is present: [Eddard Stark]."),
                rule.validationMessages(List.of(rob, attachedHonorGuard)));
    }

    @Test
    public void jaqenMysteriousPrisoner() {
        var attachedSwornSword = new ComposableEntry(swornSword, List.of(aryaAttach));

        Assertions.assertEquals(emptyList(), rule.validationMessages(List.of(jaqenAttach, attachedSwornSword)));
        Assertions.assertEquals(emptyList(), rule.validationMessages(List.of(jaqenAttach, aryaNcu)));
        Assertions.assertEquals(
                List.of("The character Jaqen H'Ghar can only be selected once",
                        "List must contain Arya Stark when including Jaqen H'Ghar, Mysterious Prisoner."),
                rule.validationMessages(List.of(jaqenAttach, jaqenAttach)));
        Assertions.assertEquals(List.of("List must contain Arya Stark when including Jaqen H'Ghar, Mysterious Prisoner."),
                rule.validationMessages(List.of(jaqenAttach)));
    }

    @Test
    public void oshaSpearWifeGuardian() {
        var invalidAttachedSwornSword = new ComposableEntry(swornSword, List.of(osha));
        var attachedSwornSword = new ComposableEntry(swornSword, List.of(rickon, osha));

        Assertions.assertEquals(emptyList(), rule.validationMessages(List.of(attachedSwornSword)));

        Assertions.assertEquals(
                List.of("List must contain Rickon Stark when including Osha, Spearwife Guardian.",
                        "Osha, Spearwife Guardian must be in the same unit as Rickon Stark."),
                rule.validationMessages(List.of(osha)));
        Assertions.assertEquals(
                List.of("List must contain Rickon Stark when including Osha, Spearwife Guardian.",
                        "Osha, Spearwife Guardian must be in the same unit as Rickon Stark."),
                rule.validationMessages(List.of(invalidAttachedSwornSword)));

        Assertions.assertEquals(List.of("Osha, Spearwife Guardian must be in the same unit as Rickon Stark."),
                rule.validationMessages(List.of(osha, rickon)));
        Assertions.assertEquals(List.of("Osha, Spearwife Guardian must be in the same unit as Rickon Stark."),
                rule.validationMessages(List.of(new ComposableEntry(swornSword, List.of(rickon)), new ComposableEntry(swornSword, List.of(osha)))));

        Assertions.assertEquals(emptyList(), rule.validationMessages(List.of(new ComposableEntry(swornSword, List.of(rickon)))));
        Assertions.assertEquals(emptyList(), rule.validationMessages(List.of(new ComposableEntry(swornSword, List.of(rickon, osha)))));
        Assertions.assertEquals(List.of("Each unit may only have one attachment, unless a unit ignores the attachment limit."),
                rule.validationMessages(List.of(new ComposableEntry(swornSword, List.of(rickon, osha, aryaAttach)))));
    }
}
