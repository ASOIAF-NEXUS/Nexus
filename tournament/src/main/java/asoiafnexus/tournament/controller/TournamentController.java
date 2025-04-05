package asoiafnexus.tournament.controller;

import asoiafnexus.tournament.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    private static final Logger LOG = LoggerFactory.getLogger(TournamentController.class);

    HashMap<UUID, Tournament> db = new HashMap<>();

    /**
     * Creates a new {@link Tournament} to be stored in the system.
     *
     * @param details initial {@link Details} data
     * @return The {@link Tournament} model containing all the input fields, plus a unique ID
     */
    @PostMapping
    public Tournament newTournament(@RequestBody Details details) {
        var id = UUID.randomUUID();

        LOG.info("New Tournament {}", details);

        var newTournament = new Tournament(
                id,
                details,
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
     * @param details New {@link Details} data being written
     * @return The updated {@link Tournament} resource
     */
    @PutMapping("/{id}")
    public Tournament updateTournament(@PathVariable UUID id, @RequestBody Details details) {
        return db.computeIfPresent(id, (i, t) ->
                new Tournament(
                        id,
                        details,
                        t.participants(),
                        t.currentPairings()));
    }

    /**
     * Adds a new {@link Participant} to the given {@link Tournament} resource
     *
     * @param id ID of the tournament resource
     * @param p  New {@link Participant} being added to the {@link Tournament}
     * @return The updated {@link Tournament} resource
     */
    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerPlayer(@PathVariable UUID id, @RequestBody Participant p) {
        if (db.get(id).started()) {
            return ResponseEntity.status(409)
                    .body("Tournament has already started");
        }

        return ResponseEntity.ok()
                .body(db.computeIfPresent(id, (i, t) ->
                        new Tournament(
                                t.id(),
                                t.details(),
                                Stream.concat(Stream.of(p), t.participants().stream()).toList(),
                                t.currentPairings())));
    }

    /**
     * Removes an existing {@link Participant} from the given {@link Tournament} resource
     *
     * @param id ID of the tournament resource
     * @param p  {@link Participant} being removed from the {@link Tournament}
     * @return The updated {@link Tournament} resource
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawPlayer(@PathVariable UUID id, @RequestBody Participant p) {
        if (db.get(id).started()) {
            return ResponseEntity.status(409)
                    .body("Tournament has already started");
        }

        return ResponseEntity.ok()
                .body(db.computeIfPresent(id, (i, t) ->
                        new Tournament(
                                t.id(),
                                t.details(),
                                t.participants().stream()
                                        .filter(x -> !x.username().equals(p.username()))
                                        .toList(),
                                t.currentPairings())));
    }

    /**
     * Starts a tournament by creating the first round of currentPairings
     *
     * @param id ID of the tournament resource
     * @return The updated {@link Tournament} resource with currentPairings
     */
    @PostMapping("/{id}/start")
    public Tournament startTournament(@PathVariable UUID id) {
        return db.computeIfPresent(id, (uuid, t) -> t.nextRound(MakePairings.RandomPairings));
    }

    /**
     * Overwrites the currentPairings to the provided list
     *
     * @param id       ID of the tournament resource
     * @param pairings List of new currentPairings. Requires all players are in the tournament
     * @return The updated {@link Tournament} resource with currentPairings
     */
    @PutMapping("/{id}/pairings")
    public ResponseEntity<?> setPairings(@PathVariable UUID id, @RequestBody List<Pairing> pairings) {
        var incomingPlayers = pairings.stream()
                .flatMap(p -> Stream.of(p.p1(), p.p2()))
                .collect(Collectors.toSet());

        var registeredPlayers = db.get(id).participants().stream().map(Participant::username).collect(Collectors.toSet());

        if (!registeredPlayers.equals(incomingPlayers)) {
            LOG.info("Registered {} incoming {}", registeredPlayers, incomingPlayers);
            return ResponseEntity.badRequest().body("Pairings did not match players registered to the tournament");
        }

        return ResponseEntity.ok()
                .body(db.computeIfPresent(id, (i, t) ->
                        new Tournament(
                                t.id(),
                                t.details(),
                                t.participants(),
                                pairings)));
    }
}
