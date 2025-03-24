package asoiafnexus.cucumber;

import asoiafnexus.tournament.model.Pairing;
import asoiafnexus.tournament.model.Player;
import asoiafnexus.tournament.model.Tournament;
import asoiafnexus.user.model.Login;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Step Definitions for actions relating to the {@link Tournament} Resource
 */
public class TournamentScenarios {
    private static final Logger LOG = LoggerFactory.getLogger(TournamentScenarios.class);

    NexusClient client = NexusClient.instance;

    /**
     * From the list of all tournaments in the system, find the one that matches the expected name
     * and return it if found.
     * <p>
     * Used to either assert an initial expectation, or in order to find the tournament UUID.
     */
    private Optional<Tournament> tournamentByName(String name) throws IOException {
        return client.getAllTournaments().stream()
                .filter(t -> Objects.requireNonNull(name).equals(t.name()))
                .findAny();
    }

    @Given("a tournament named {string}")
    public void tournamentExistsByName(String name) throws IOException {
        Assertions.assertTrue(tournamentByName(name).isPresent());
    }

    /**
     * Utility for creating a {@link Tournament} instance from a Cucumber {@link DataTable}
     */
    public Tournament toTournament(DataTable table) {
        var inputs = table.asMaps().getFirst();
        var notBlank = Predicate.not(String::isBlank);
        return new Tournament(
                Optional.ofNullable(inputs.get("id"))
                        .filter(notBlank)
                        .map(UUID::fromString)
                        .orElse(null),
                Optional.ofNullable(inputs.get("name"))
                        .filter(notBlank)
                        .orElse(null),
                Optional.ofNullable(inputs.get("description"))
                        .filter(notBlank)
                        .orElse(null),
                Optional.ofNullable(inputs.get("location"))
                        .filter(notBlank)
                        .orElse(null),
                Optional.ofNullable(inputs.get("datetime"))
                        .filter(notBlank)
                        .map(ZonedDateTime::parse)
                        .orElse(null),
                Collections.emptyList(),
                Collections.emptyList());
    }

    /**
     * Utility for helping log when an error is returned from the server.
     */
    private void logBodyIfPresent(ResponseBody body) {
        Optional.ofNullable(body)
                .filter(b -> b.contentLength() > 0)
                .map(b -> {
                    try {
                        return b.string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).ifPresent(LOG::error);
    }

    @Given("the tournament {string} is already started")
    public void tournamentStarted(String name) throws IOException {
        var tournament = tournamentByName(name).orElseThrow();
        Assertions.assertTrue(tournament.started());
    }

    @When("a tournament is created with the information")
    public void createTournament(DataTable table) throws IOException {
        var tournament = toTournament(table);

        client.createTournament(tournament, response -> {
            logBodyIfPresent(response.body());
            Assertions.assertTrue(response.isSuccessful());
        });
    }

    @When("tournament details for {string} are updated")
    public void updateTournament(String name, DataTable table) throws IOException {
        var existing = tournamentByName(name).orElseThrow();
        var newDetails = toTournament(table);
        var merged = new Tournament(
                existing.id(),
                newDetails.name(),
                newDetails.description(),
                newDetails.location(),
                newDetails.datetime(),
                newDetails.players(),
                newDetails.pairings());

        client.updateTournament(merged, response -> {
            logBodyIfPresent(response.body());
            Assertions.assertTrue(response.isSuccessful());
        });
    }

    @When("a player signs up for the tournament {string}")
    public void playersSignup(String name, DataTable players) throws IOException {
        var tournament = tournamentByName(name).orElseThrow();
        players.asMaps().forEach(p -> {
            var username = p.get("username");
            var player = new Player(username);
            try {
                client.withToken(client.loginUser(username));
                client.tournamentSignup(tournament, player, response -> {
                    logBodyIfPresent(response.body());
                    Assertions.assertTrue(response.isSuccessful());
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @When("a player withdraws from the tournament {string}")
    public void playersWithdraw(String name, DataTable players) throws IOException {
        var tournament = tournamentByName(name).orElseThrow();
        players.asMaps().forEach(p -> {
            var username = p.get("username");
            var player = new Player(username);
            try {
                client.withToken(client.loginUser(username));
                client.tournamentWithdraw(tournament, player, response -> {
                    logBodyIfPresent(response.body());
                    Assertions.assertTrue(response.isSuccessful());
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @When("the tournament {string} is started")
    public void startTournament(String name) throws IOException {
        var tournament = tournamentByName(name).orElseThrow();
        client.tournamentStart(tournament, response -> {
            logBodyIfPresent(response.body());
            Assertions.assertTrue(response.isSuccessful());
        });
    }

    public List<Pairing> toPairings(DataTable input) {
        return input.asMaps().stream()
                .map(p -> new Pairing(
                        new Player(p.get("p1")),
                        new Player(p.get("p2"))))
                .toList();
    }

    @When("pairings are provided to the tournament {string}")
    public void setPairings(String name, DataTable input) throws IOException {
        var tournament = tournamentByName(name).orElseThrow();
        var pairings = toPairings(input);

        client.tournamentSetPairings(tournament, pairings, response -> {
            logBodyIfPresent(response.body());
            Assertions.assertTrue(response.isSuccessful());
        });
    }

    @Then("a tournament is in the list of all tournaments")
    public void verifyTournamentExists(DataTable table) throws IOException {
        var target = toTournament(table);
        var tournaments = client.getAllTournaments();
        LOG.info("Target: {}, Result: {}", target, tournaments);
        Assertions.assertTrue(tournaments.stream()
                .map(x -> new Tournament(null, x.name(), x.description(), x.location(), x.datetime(), x.players(), x.pairings()))
                .anyMatch(target::equals));
    }

    @Then("the tournament {string} shows a list of participants")
    public void verifyTournamentParticipants(String name, DataTable players) throws IOException {
        var tournament = tournamentByName(name).orElseThrow();
        var expected = players.asMaps().stream()
                .map(x -> x.get("username"))
                .collect(Collectors.toSet());
        var usernames = tournament.players().stream()
                .map(Player::username)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expected, usernames);
    }

    @Then("{int} random pairings are created for the tournament {string}")
    public void countRandomPairings(Integer num, String name) throws IOException {
        var tournament = tournamentByName(name).orElseThrow();
        LOG.info("Pairings: {}", tournament.pairings());
        Assertions.assertEquals(num, tournament.pairings().size());
    }

    @Then("any player can no longer sign up for the tournament {string}")
    public void signupFails(String name) throws IOException {
        var tournament = tournamentByName(name).orElseThrow();
        var player = new Player(UUID.randomUUID().toString());

        client.tournamentSignup(tournament, player, response -> {
            logBodyIfPresent(response.body());
            Assertions.assertFalse(response.isSuccessful());
        });
    }

    @Then("the latest pairings for the tournament {string} are")
    public void pairingsMatch(String name, DataTable input) throws IOException {
        var pairings = toPairings(input);
        var tournament = tournamentByName(name).orElseThrow();

        Assertions.assertEquals(
                new HashSet<>(pairings),
                new HashSet<>(tournament.pairings()));
    }
}
