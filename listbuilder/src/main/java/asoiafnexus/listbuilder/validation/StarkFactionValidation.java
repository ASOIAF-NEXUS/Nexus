package asoiafnexus.listbuilder.validation;

import java.util.List;
import java.util.Set;

public class StarkFactionValidation {
    public final ValidationRule STARK_CHARACTERS = new CharacterRule(
            Set.of("Eddard Stark",
                    "Rodrik Cassel",
                    "Howland Reed",
                    "Arya Stark",
                    "Lyanna Mormont",
                    "Rob Stark",
                    "Catelyn Stark",
                    "Sansa Stark",
                    "Bran And Hodor",
                    "Brynden Tully",
                    "Greatjon Umber",
                    "Jaqen H'Ghar",
                    "Jojen Reen",
                    "Maege Mormont",
                    "Meera Reed",
                    "Osha",
                    "Rickard Karstark",
                    "Rickon Stark",
                    "Syrio Forel",
                    "Eddard's Honor Guard",
                    "Grey Wind",
                    "Shaggydog",
                    "Summer"
            )
    );
    public final ValidationRule ROB_STARK_KING_IN_THE_NORTH = new ExclusionRule(new EntryMatcher("Rob Stark", "King In The North"), List.of(new EntryMatcher("Eddard Stark")));
    public final ValidationRule JAQEN_HGHAR_MYSTERIOUS_PRISONER = new RequiresEntryRule(new EntryMatcher("Jaqen H'Ghar", "Mysterious Prisoner"), new EntryMatcher("Arya Stark"));
    public final List<ValidationRule> OSHA_SPEARWIFE_GUARDIAN = List.of(
            new RequiresEntryRule(new EntryMatcher("Osha", "Spearwife Guardian"), new EntryMatcher("Rickon Stark")),
            new ComposedTogetherRule(new EntryMatcher("Osha", "Spearwife Guardian"), new EntryMatcher("Rickon Stark"))
    );
    public final ValidationRule SYRIO_FOREL_FIRST_BLADE_OF_BRAAVOS = new RequiresEntitiesRule(
            new EntryMatcher("Syrio Forel"),
            List.of(new EntryMatcher("Eddard Stark"), new EntryMatcher("Arya Stark", "The Wolf Girl")),
            RequiresEntitiesRule.OpType.OR);
    public final List<ValidationRule> EDDARDS_HONOR_GUARD = List.of(
            new RequiresEntryRule(new EntryMatcher("Eddard's Honor Guard"), new EntryMatcher("Eddard Stark", "Lord Of Winterfell")),
            new ComposedTogetherRule(new EntryMatcher("Eddard's Honor Guard"), new EntryMatcher("Eddard Stark", "Lord Of Winterfell"))
    );
    public final ValidationRule GREYWIND = new RequiresEntryRule(new EntryMatcher("Grey Wind"), new EntryMatcher("Rob Stark"));
    public ValidationRule SHAGGYDOG = new RequiresEntryRule(new EntryMatcher("Shaggydog"), new EntryMatcher("Rickon Stark"));
    public final ValidationRule SUMMER = new RequiresEntryRule(new EntryMatcher("Summer"), new EntryMatcher(".*Bran Stark.*"));
    public final ValidationRule ATTACHMENT_IGNORE_LIMIT = new OneAttachmentRule(List.of(new EntryMatcher("Osha", "Spearwife Guardian")));

    final ValidationRule STARK_FACTION_VALIDATION = ValidationRule.comp(
            STARK_CHARACTERS,
            ROB_STARK_KING_IN_THE_NORTH,
            JAQEN_HGHAR_MYSTERIOUS_PRISONER,
            //OSHA_SPEARWIFE_GUARDIAN,
            SYRIO_FOREL_FIRST_BLADE_OF_BRAAVOS,
            // EDDARDS_HONOR_GUARD,
            GREYWIND,
            SHAGGYDOG,
            SUMMER,
            ATTACHMENT_IGNORE_LIMIT
    );
}