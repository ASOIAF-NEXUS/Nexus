package asoiafnexus.listbuilder;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static asoiafnexus.listbuilder.Units.Faction.*;
import static asoiafnexus.listbuilder.Units.ListBuildingAbilities.*;
import static asoiafnexus.listbuilder.Units.UnitType.*;

/**
 * Functions and capabilities for interacting with unit data.
 */
public class Units {
    public enum Faction {LANNISTER, STARKS}

    public enum BattlefieldRole {UNIT, ATTACHMENT, ENEMY_ATTACHMENT}
    public enum UnitType {INFANTRY, CAVALRY, MONSTER, WAR_MACHINE, NCU}

    public enum ListBuildingAbilities {ADAPTIVE, CHARACTER, COMMANDER, SOLO}

    public record Unit(
            Faction faction,
            String name,
            String title,
            String id,
            UnitType type,
            int points,
            List<ListBuildingAbilities> buildingAbilities
    ) {
    }

    static class UnitBuilder {
        Faction faction;
        BattlefieldRole battlefieldRole;
        UnitType unitType;
        LinkedList<Unit> units;

        UnitBuilder() {
            faction = null;
            battlefieldRole = null;
            unitType = null;
            units = new LinkedList<>();
        }

        UnitBuilder forFaction(Faction f) {
            this.faction = f;
            return this;
        }

        UnitBuilder forBattlefieldRole(BattlefieldRole r) {
            this.battlefieldRole = r;
            return this;
        }

        UnitBuilder forUnits(UnitType u) {
            this.unitType = u;
            return this;
        }

        UnitBuilder define(String name, int points) {
            return define(name, null, points, Collections.emptyList());
        }

        UnitBuilder define(String name, String title, int points) {
            return define(name, title, points, Collections.emptyList());
        }

        UnitBuilder define(String name, String title, int points, List<ListBuildingAbilities> buildingAbilities) {
            units.add(new Unit(this.faction, name, title, makeId(name, title), this.unitType, points, buildingAbilities));
            return this;
        }

        List<Unit> build() {
            return units;
        }

        private String makeId(String name, String title) {
            var combined = Optional.ofNullable(name).orElse("") +
                    Optional.ofNullable(title).orElse("");
            return combined.toLowerCase().replaceAll("\\w", "");
        }
    }

    public static List<Unit> allUnits() {
        return new UnitBuilder()
                .forFaction(STARKS)
                .forBattlefieldRole(BattlefieldRole.UNIT)
                .forUnits(CAVALRY)
                .define("House Tully Cavaliers", 8)
                .define("House Umber Ravagers", 7)
                .define("Stark Outriders", 6)

                .forUnits(INFANTRY)
                .define("Crannogman Bog Devils", 7)
                .define("Crannogman Trackers", 5)
                .define("Eddardd's Honor Guard", null, 6, List.of(CHARACTER))
                .define("House Karstark Loyalists", 5)
                .define("House Karstark Spearmen", 5)
                .define("House Mormont Bruisers", 6)
                .define("House Mormont She-Bears", 6)
                .define("House Tully Sworn Shield", 6)
                .define("House Umber Berserkers", 6)
                .define("House Umber Greataxes", 7)
                .define("Stark Bowmen", 6)
                .define("Stark Sworn Swords", 5)
                .define("Winterfell Guard", null, 7, List.of(ADAPTIVE))

                .forUnits(MONSTER)
                .define("Grey Wind", null, 3, List.of(CHARACTER, SOLO))
                .define("Shaggydog", null, 3, List.of(CHARACTER, SOLO))
                .define("Summer", null, 3, List.of(CHARACTER, SOLO))

                .build();
    }
}
