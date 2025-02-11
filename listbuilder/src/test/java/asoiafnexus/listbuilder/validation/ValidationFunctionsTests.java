package asoiafnexus.listbuilder.validation;

import asoiafnexus.listbuilder.Units;
import asoiafnexus.listbuilder.model.ArmyList;
import asoiafnexus.listbuilder.model.ComposableEntry;
import asoiafnexus.listbuilder.model.Unit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static asoiafnexus.listbuilder.model.Faction.stark;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationFunctionsTests {

    private Map<String, Unit> units = new Units(new ObjectMapper()).unitLookup();

    @Test
    public void invalidUnitFaction() {
        assertEquals(emptyList(), ValidationFunctions.invalidUnitFaction(
                new ArmyList(stark, emptyList())));

        assertEquals(emptyList(), ValidationFunctions.invalidUnitFaction(
                new ArmyList(stark, List.of(
                        new ComposableEntry(units.get("starkswornswords"),
                                List.of(units.get("swornswordcaptain")))))));

        assertEquals(List.of("Lannister Guardsmen can not be included in a stark army list."), ValidationFunctions.invalidUnitFaction(
                new ArmyList(stark, List.of(
                        new ComposableEntry(units.get("lannisterguardsmen"),
                                List.of(units.get("swornswordcaptain")))))));

        assertEquals(List.of("Only 30% of the army list can be from the neutral faction."), ValidationFunctions.invalidUnitFaction(
                new ArmyList(stark, List.of(
                        new ComposableEntry(units.get("houseboltoncutthroats"),
                                List.of(units.get("swornswordcaptain")))))));
    }

    @Test
    public void duplicateCharacters() {
        assertEquals(emptyList(), ValidationFunctions.duplicateCharacters(
                new ArmyList(stark, emptyList())));

        assertEquals(emptyList(), ValidationFunctions.duplicateCharacters(
                new ArmyList(stark, List.of(
                        new ComposableEntry(units.get("starkswornswords"),
                                List.of(units.get("swornswordcaptain")))))));

        assertEquals(List.of("Character Eddard Stark may only appear in the army once."), ValidationFunctions.duplicateCharacters(
                new ArmyList(stark, List.of(
                        new ComposableEntry(units.get("starkswornswords"),
                                List.of(units.get("eddardstarklordofwinterfell"))),
                        new ComposableEntry(units.get("eddardstarkhandoftheking"),
                                emptyList()),
                        new ComposableEntry(units.get("eddardstarkwardenofthenorth"),
                                emptyList())))));
    }
}
