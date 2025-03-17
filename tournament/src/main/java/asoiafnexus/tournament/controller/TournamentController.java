package asoiafnexus.tournament.controller;

import asoiafnexus.tournament.model.Tournament;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    HashMap<UUID, Tournament> db = new HashMap<>();

    /**
     * Creates a new {@link Tournament} to be stored in the system.
     *
     * @param t initial {@link Tournament} data
     * @return The {@link Tournament} model containing all the input fields, plus a unique ID
     */
    @PostMapping
    public Tournament newTournament(@RequestBody Tournament t) {
        var id = UUID.randomUUID();
        var newTournament = new Tournament(id, t.name(), t.description(), t.location(), t.datetime(), t.players());
        db.put(id, newTournament);
        return newTournament;
    }

    /**
     * @return All of the {@link Tournament} resources saved in the system.
     * NOTE: This will likely include some kind of filtering options moving forward
     */
    @GetMapping
    public List<Tournament> allTournaments() {
        return db.values().stream().toList();
    }

    /**
     * @return A single {@link Tournament} resource saved in the system.
     */
    @GetMapping("/{id}")
    public Tournament tournamentById(@RequestParam UUID id) {
        return db.get(id);
    }

    /**
     * Overwrites the {@link Tournament} data for the given resource
     * @param id ID of the resource being overwritten
     * @param t  New {@link Tournament} data being written
     * @return The updated {@link Tournament} resource
     */
    @PutMapping("/{id}")
    public Tournament updateTournament(@RequestParam UUID id, @RequestBody Tournament t) {
        db.put(id, t);
        return db.get(id);
    }
}
