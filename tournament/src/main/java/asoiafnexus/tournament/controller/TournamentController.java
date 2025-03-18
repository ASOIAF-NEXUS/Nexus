package asoiafnexus.tournament.controller;

import asoiafnexus.tournament.model.MakePairings;
import asoiafnexus.tournament.model.Pairing;
import asoiafnexus.tournament.model.Player;
import asoiafnexus.tournament.model.Tournament;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    HashMap<UUID, Tournament> db = new HashMap<>();

    public record TournamentInput(
            String name,
            String description,
            String location,
            ZonedDateTime datetime) {
    }

    /**
     * Creates a new {@link Tournament} to be stored in the system.
     *
     * @param input initial {@link TournamentInput} data
     * @return The {@link Tournament} model containing all the input fields, plus a unique ID
     */
    @PostMapping
    public Tournament newTournament(@RequestBody TournamentInput input) {
        var id = UUID.randomUUID();
        var newTournament = new Tournament(
                id,
                input.name(),
                input.description(),
                input.location(),
                input.datetime(),
                Collections.emptyList(),
                Collections.emptyList());
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
    public Tournament tournamentById(@PathVariable UUID id) {
        return db.get(id);
    }

    /**
     * Overwrites the {@link Tournament} data for the given resource
     *
     * @param id    ID of the resource being overwritten
     * @param input New {@link TournamentInput} data being written
     * @return The updated {@link Tournament} resource
     */
    @PutMapping("/{id}")
    public Tournament updateTournament(@PathVariable UUID id, @RequestBody TournamentInput input) {
        return db.computeIfPresent(id, (x, t) ->
                new Tournament(
                        id,
                        input.name(),
                        input.description(),
                        input.location(),
                        input.datetime(),
                        t.players(),
                        t.pairings()));
    }

    /**
     * Adds a new {@link Player} to the given {@link Tournament} resource
     *
     * @param id ID of the tournament resource
     * @param p  New {@link Player} being added to the {@link Tournament}
     * @return The updated {@link Tournament} resource
     */
    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerPlayer(@PathVariable UUID id, @RequestBody Player p) {
        if (db.get(id).started()) {
            return ResponseEntity.status(409)
                    .body("Tournament has already started");
        }

        return ResponseEntity.ok()
                .body(db.computeIfPresent(id, (x, t) ->
                        new Tournament(
                                t.id(),
                                t.name(),
                                t.description(),
                                t.location(),
                                t.datetime(),
                                Stream.concat(Stream.of(p), t.players().stream()).toList(),
                                t.pairings())));
    }

    /**
     * Removes an existing {@link Player} from the given {@link Tournament} resource
     *
     * @param id ID of the tournament resource
     * @param p  {@link Player} being removed from the {@link Tournament}
     * @return The updated {@link Tournament} resource
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawPlayer(@PathVariable UUID id, @RequestBody Player p) {
        if (db.get(id).started()) {
            return ResponseEntity.status(409)
                    .body("Tournament has already started");
        }

        return ResponseEntity.ok()
                .body(db.computeIfPresent(id, (x, t) ->
                        new Tournament(
                                t.id(),
                                t.name(),
                                t.description(),
                                t.location(),
                                t.datetime(),
                                t.players().stream()
                                        .filter(x -> !x.username().equals(p.username()))
                                        .toList(),
                                t.pairings())));
    }

    /**
     * Starts a tournament by creating the first round of pairings
     *
     * @param id ID of the tournament resource
     * @return The updated {@link Tournament} resource with pairings
     */
    @PostMapping("/{id}/start")
    public Tournament startTournament(@PathVariable UUID id) {
        return db.computeIfPresent(id, (x, t) ->
                new Tournament(
                        t.id(),
                        t.name(),
                        t.description(),
                        t.location(),
                        t.datetime(),
                        t.players(),
                        t.newPairings(MakePairings.RandomPairings)));
    }

    /**
     * Overwrites the pairings to the provided list
     *
     * @param id       ID of the tournament resource
     * @param pairings List of new pairings. Requires all players are in the tournament
     * @return The updated {@link Tournament} resource with pairings
     */
    @PutMapping("/{id}/pairings")
    public ResponseEntity<?> setPairings(@PathVariable UUID id, @RequestBody List<Pairing> pairings) {
        var incomingPlayers = pairings.stream()
                .flatMap(p -> Stream.of(p.p1(), p.p2()))
                .collect(Collectors.toSet());

        var registeredPlayers = new HashSet<>(db.get(id).players());

        if (!registeredPlayers.equals(incomingPlayers)) {
            return ResponseEntity.badRequest().body("Pairings did not match players registered to the tournament");
        }

        return ResponseEntity.ok()
                .body(db.computeIfPresent(id, (x, t) ->
                        new Tournament(
                                t.id(),
                                t.name(),
                                t.description(),
                                t.location(),
                                t.datetime(),
                                t.players(),
                                pairings)));
    }
}
