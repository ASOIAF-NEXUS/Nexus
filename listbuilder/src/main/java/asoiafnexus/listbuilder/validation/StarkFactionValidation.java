package asoiafnexus.listbuilder.validation;

import java.util.List;
import java.util.Set;

public class StarkFactionValidation {
    public final List<ValidationRule> STARK_VALIDATION_RULES = List.of(
            new CharacterRule(
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
            ),
            new ExclusionRule(new EntryMatcher("Rob Stark", "King In The North"), List.of(new EntryMatcher("Eddard Stark"))),
            new RequiresEntryRule(new EntryMatcher("Jaqen H'Ghar", "Mysterious Prisoner"), new EntryMatcher("Arya Stark")),
            new RequiresEntryRule(new EntryMatcher("Osha", "Spearwife Guardian"), new EntryMatcher("Rickon Stark")),
            new ComposedTogetherRule(new EntryMatcher("Osha", "Spearwife Guardian"), new EntryMatcher("Rickon Stark")),
            new RequiresEntitiesRule(
                    new EntryMatcher("Syrio Forel"),
                    List.of(new EntryMatcher("Eddard Stark"), new EntryMatcher("Arya Stark", "The Wolf Girl")),
                    RequiresEntitiesRule.OpType.OR),
            new RequiresEntryRule(new EntryMatcher("Eddard's Honor Guard"), new EntryMatcher("Eddard Stark", "Lord Of Winterfell")),
            new ComposedTogetherRule(new EntryMatcher("Eddard's Honor Guard"), new EntryMatcher("Eddard Stark", "Lord Of Winterfell")),
            new RequiresEntryRule(new EntryMatcher("Grey Wind"), new EntryMatcher("Rob Stark")),
            new RequiresEntryRule(new EntryMatcher("Shaggydog"), new EntryMatcher("Rickon Stark")),
            new RequiresEntryRule(new EntryMatcher("Summer"), new EntryMatcher(".*Bran Stark.*")),
            new OneAttachmentRule(List.of(new EntryMatcher("Osha", "Spearwife Guardian"))));

    final ValidationRule STARK_VALIDATORS = ValidationRule.comp(STARK_VALIDATION_RULES.toArray(new ValidationRule[0]));
}