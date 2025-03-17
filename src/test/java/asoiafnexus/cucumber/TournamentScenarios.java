package asoiafnexus.cucumber;

import asoiafnexus.tournament.model.Tournament;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

public class TournamentScenarios {
    private static final Logger LOG = LoggerFactory.getLogger(TournamentScenarios.class);

    NexusClient client = new NexusClient();

    @Given("a user {string}")
    public void givenUser(String userName) {
        LOG.info(userName);
    }

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
                null);
    }

    @When("a tournament is created with the information")
    public void createTournament(DataTable table) throws IOException {
        var tournament = toTournament(table);

        client.createTournament(tournament, response -> {
            Optional.ofNullable(response.body())
                    .filter(b -> b.contentLength() > 0)
                    .map(b -> {
                        try {
                            return b.string();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).ifPresent(LOG::info);

            Assertions.assertTrue(response.isSuccessful());
        });
    }

    @Then("a tournament is in the list of all tournaments")
    public void verifyTournamentExists(DataTable table) throws IOException {
        var target = toTournament(table);
        var tournaments = client.getAllTournaments();
        LOG.info("Target: {}, Result: {}", target, tournaments);
        Assertions.assertTrue(tournaments.stream()
                .map(x -> new Tournament(null, x.name(), x.description(), x.location(), x.datetime(), null))
                .anyMatch(target::equals));
    }
}
