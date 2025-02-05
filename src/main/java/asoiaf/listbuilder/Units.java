package asoiaf.listbuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static asoiaf.listbuilder.Units.Faction.LANNISTER;

/**
 * Functions and capabilities for interacting with unit data.
 */
public class Units {
    public enum Faction {LANNISTER}
    public enum UnitType {COMBAT_UNIT, NCU, ATTACHMENT}
    public enum ListBuildingAbilities {ADAPTIVE, KINGSGUARD}

    public record Unit (
            Faction faction,
            String name,
            String title,
            UnitType type,
            int points,
            List<ListBuildingAbilities> buildingAbilities
    ) {}

    static class UnitBuilder {
        Faction faction;
        UnitType unitType;
        LinkedList<Unit> units;

        UnitBuilder() {
            faction = null;
            unitType = null;
            units = new LinkedList<>();
        }

        UnitBuilder forFaction(Faction f) { this.faction = f; return this; }
        UnitBuilder forUnits(UnitType u) { this.unitType = u; return this; }
        UnitBuilder define(String name, int points) { return define(name, null, points, Collections.emptyList()); }
        UnitBuilder define(String name, String title, int points) { return define(name, title, points, Collections.emptyList()); }
        UnitBuilder define(String name, String title, int points, List<ListBuildingAbilities> buildingAbilities) {
            units.add(new Unit(this.faction, name, title, this.unitType, points, buildingAbilities));
            return this;
        }

        List<Unit> build() {
            return units;
        }
    }

    public static List<Unit> allUnits() {
        return new Units.UnitBuilder()
                .forFaction(LANNISTER)
                .forUnits(UnitType.COMBAT_UNIT)
                .define("Gregor Clegane", "The Mountain That Rides", 4)
                .define("Poor Fellows", 4)
                .define("Lannister Guardsmen", 4)
                .define("Lannister Halberdiers", 5)
                .define("Gold Cloaks", 5)
                .define("House Clegane Mountain's Men", 6)
                .define("Lannister Crossbowmen", 6)
                .define("Red Cloaks", 6)
                .define("Lannisport City Watch", 6)
                .define("House Clegane Brigands", 6)
                .define("Pyromancers", 7)
                .define("The Warrior's Sons", 7)
                .define("Casterly Rock Honor Guard", 7)
                .define("Knights Of Casterly Rock", 8)

                .forUnits(UnitType.ATTACHMENT)
                .define("Clegane Butcher", 1)
                .define("Gregor Clegane", "Mounted Behemoth", 3)
                .define("Tyrion Lannister", "The Giant Of Lannister", 1)
                .define("Sandor Clegane", "The Hound", 1)
                .define("Preston Greenfield", "Kingsguard", 1, List.of(ListBuildingAbilities.KINGSGUARD))
                .define("Meryn Trant", "Kingsguard",1, List.of(ListBuildingAbilities.KINGSGUARD))
                .define("Arys Oakheart", "Kingsguard",1, List.of(ListBuildingAbilities.KINGSGUARD))
                .define("Mandon Moore", "Kingsguard",1, List.of(ListBuildingAbilities.KINGSGUARD))
                .define("Boros Blount", "Kingsguard",1, List.of(ListBuildingAbilities.KINGSGUARD))
                .define("Qyburn", "Forbidden Knowledge", 1)
                .define("Champion of the Faith", 1)
                .define("Guard Captain", 1)
                .define("Assault Veteran", 1)
                .define("Sentinel Enforcer", 1)
                .define("Jaime Lannister", "Kingsguard",1, List.of(ListBuildingAbilities.KINGSGUARD))
                .define("Jamie Lannister", "The Young Lion", 2)
                .define("Barristan Selmy", "Lord Commander Of The Kingsguard", 2, List.of(ListBuildingAbilities.KINGSGUARD))
                .define("Gregor Clegane", "Lord Tywin's Mad Dog", 2)
                .define("Addam Marbrand", "Trusted Bannerman", 2)
                .build();
    }
}
