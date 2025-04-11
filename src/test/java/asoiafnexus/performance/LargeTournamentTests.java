package asoiafnexus.performance;

import asoiafnexus.Main;
import asoiafnexus.tournament.model.*;
import asoiafnexus.user.model.Login;
import asoiafnexus.user.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Main.class, loader = SpringBootContextLoader.class)
public class LargeTournamentTests {
    private static final Logger LOG = LoggerFactory.getLogger(LargeTournamentTests.class);

    @LocalServerPort
    private int port;

    private final Random random = new Random();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public record UserContext(Login login, String token, User user) {
    }

    @Test
    public void runLargeTournament() {
        var numParticipants = 200;
        var eventOrganizer = createUser(new Login("TO", "ImReallyImportant"));
        var event = createTournament(eventOrganizer);
        var participants = IntStream.range(0, numParticipants)
                .parallel()
                .mapToObj(idx -> new Login("participant-" + idx, "Password123!"))
                .map(l -> createUser(l, event))
                .collect(Collectors.toMap(u -> u.user.id(), Function.identity()));

        var startedEvent = startTournament(eventOrganizer, event);
        Assertions.assertEquals(
                participants.keySet().stream().sorted().toList(),
                startedEvent.state().currentPairings().stream()
                        .flatMap(p -> Stream.of(p.p1(), p.p2()))
                        .sorted()
                        .toList());
        LOG.info("Initial Pairings: {}", startedEvent.state().getCurrentPairings());


        var round1Results = startedEvent.state().currentPairings().stream()
                .flatMap(p -> randomResult(p, startedEvent.state().round()).stream())
                .collect(Collectors.toList());
        Collections.shuffle(round1Results);
        round1Results.stream()
                .parallel()
                .forEach(r -> submitResult(participants, event, r));

        var eventRound2 = startNextRound(eventOrganizer, event);
        Assertions.assertEquals(
                participants.keySet().stream().sorted().toList(),
                eventRound2.state().currentPairings().stream()
                        .flatMap(p -> Stream.of(p.p1(), p.p2()))
                        .sorted()
                        .toList());

        LOG.info("Next Round Pairings: {}", startedEvent.state().getCurrentPairings());

        var round2Results = eventRound2.state().currentPairings().stream()
                .flatMap(p -> randomResult(p, eventRound2.state().round()).stream())
                .collect(Collectors.toList());
        Collections.shuffle(round2Results);
        round2Results.stream()
                .parallel()
                .forEach(r -> submitResult(participants, event, r));
    }

    public List<Result> randomResult(Pairing pairing, int round) {
        var choice = random.nextInt(6);
        if (pairing.bye()) {
            LOG.info("{} bye round", pairing.p1());
            return List.of(new Result(pairing.p1(), pairing.p2(), round, List.of(3, 4, 0)));
        }
        switch (choice) {
            case 0: // p1 crushing
                LOG.info("{} crushing win against {}", pairing.p1(), pairing.p2());
                return List.of(
                        new Result(pairing.p1(), pairing.p2(), round, List.of(3, 4, random.nextInt(28))),
                        new Result(pairing.p2(), pairing.p1(), round, List.of(1, 0, random.nextInt(28))));
            case 1: // p1 major
                LOG.info("{} standard win against {}", pairing.p1(), pairing.p2());
                return List.of(
                        new Result(pairing.p1(), pairing.p2(), round, List.of(3, 3, random.nextInt(28))),
                        new Result(pairing.p2(), pairing.p1(), round, List.of(1, 1, random.nextInt(28))));
            case 2: // p1 minor
                LOG.info("{} narrow win against {}", pairing.p1(), pairing.p2());
                return List.of(
                        new Result(pairing.p1(), pairing.p2(), round, List.of(3, 2, random.nextInt(28))),
                        new Result(pairing.p2(), pairing.p1(), round, List.of(1, 2, random.nextInt(28))));
            case 3: // p2 minor
                LOG.info("{} narrow loss against {}", pairing.p1(), pairing.p2());
                return List.of(
                        new Result(pairing.p1(), pairing.p2(), round, List.of(1, 2, random.nextInt(28))),
                        new Result(pairing.p2(), pairing.p1(), round, List.of(3, 2, random.nextInt(28))));
            case 4: // p2 major
                LOG.info("{} standard loss against {}", pairing.p1(), pairing.p2());
                return List.of(
                        new Result(pairing.p1(), pairing.p2(), round, List.of(1, 1, random.nextInt(28))),
                        new Result(pairing.p2(), pairing.p1(), round, List.of(3, 3, random.nextInt(28))));
            case 5: // p2 crushing
                LOG.info("{} crushing loss against {}", pairing.p1(), pairing.p2());
                return List.of(
                        new Result(pairing.p1(), pairing.p2(), round, List.of(1, 0, random.nextInt(28))),
                        new Result(pairing.p2(), pairing.p1(), round, List.of(3, 4, random.nextInt(28))));
            default:
                LOG.info("{} and {} tied", pairing.p1(), pairing.p2());
                return List.of(
                        new Result(pairing.p1(), pairing.p2(), round, List.of(2, 2, random.nextInt(28))),
                        new Result(pairing.p2(), pairing.p1(), round, List.of(2, 2, random.nextInt(28))));
        }
    }

    // HTTP
    private final OkHttpClient client = new OkHttpClient();

    public UserContext createUser(Login login) {
        LOG.info("Creating User: {}", login.username());
        try {
            var signupRequest = new Request.Builder()
                    .post(RequestBody.create(
                            objectMapper.writeValueAsString(login),
                            MediaType.get("application/json")))
                    .url(String.format("http://localhost:%d/api/v1/users/signup", port))
                    .build();
            try (var response = client.newCall(signupRequest).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Unable to signup " + login.username());
            }

            var loginRequest = new Request.Builder()
                    .post(RequestBody.create(
                            objectMapper.writeValueAsString(login),
                            MediaType.get("application/json")))
                    .url(String.format("http://localhost:%d/api/v1/users/login", port))
                    .build();

            String token = null;
            try (var response = client.newCall(loginRequest).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Unable to login " + login.username());
                token = response.body().string();
            }

            var profileRequest = new Request.Builder()
                    .get()
                    .header("Authorization", "Bearer " + token)
                    .url(String.format("http://localhost:%d/api/v1/users/me", port))
                    .build();

            User user = null;
            try (var response = client.newCall(profileRequest).execute()) {
                user = objectMapper.readValue(response.body().byteStream(), new TypeReference<>() {
                });
            }

            return new UserContext(login, token, user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserContext createUser(Login login, Tournament event) {
        try {
            var context = createUser(login);

            var registerRequest = new Request.Builder()
                    .post(RequestBody.create(
                            objectMapper.writeValueAsString(new Participant(context.user.id(), Collections.emptyList())),
                            MediaType.get("application/json")))
                    .header("Authorization", "Bearer " + context.token)
                    .url(String.format("http://localhost:%d/api/v1/tournaments/%s/register",
                            port,
                            event.id()))
                    .build();

            try (var response = client.newCall(registerRequest).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Unable to register " + login.username());
            }

            return context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Tournament createTournament(UserContext tournamentOrganizer) {
        try {
            var details = new Details(
                    "Large Test Tournament",
                    "This is a performance test where a large number of participants and rounds are simulated",
                    "Local Machine",
                    Instant.now().atZone(ZoneId.systemDefault())
            );
            var request = new Request.Builder()
                    .post(RequestBody.create(
                            objectMapper.writeValueAsString(details),
                            MediaType.get("application/json")))
                    .header("Authorization", "Bearer " + tournamentOrganizer.token)
                    .url(String.format("http://localhost:%d/api/v1/tournaments", port))
                    .build();

            try (var response = client.newCall(request).execute()) {
                return objectMapper.readValue(response.body().byteStream(), new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Tournament startTournament(UserContext eventOrganizer, Tournament event) {
        LOG.info("Starting Tournament");

        try {
            var request = new Request.Builder()
                    .post(RequestBody.create(
                            objectMapper.writeValueAsString(""),
                            MediaType.get("application/json")
                    ))
                    .header("Authorization", "Bearer " + eventOrganizer.token)
                    .url(String.format("http://localhost:%d/api/v1/tournaments/%s/start",
                            port,
                            event.id()))
                    .build();

            try (var response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Unable to start tournament");
                return objectMapper.readValue(response.body().byteStream(), new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Tournament submitResult(Map<UUID, UserContext> context, Tournament event, Result result) {
        LOG.info("Submitting result: {}", result);
        try {
            var request = new Request.Builder()
                    .post(RequestBody.create(
                            objectMapper.writeValueAsString(result),
                            MediaType.get("application/json")
                    ))
                    .header("Authorization", "Bearer " + context.get(result.participant()).token)
                    .url(String.format("http://localhost:%d/api/v1/tournaments/%s/pairings/result",
                            port,
                            event.id()))
                    .build();

            try (var response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Unable to submit result");
                return objectMapper.readValue(response.body().byteStream(), new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Tournament startNextRound(UserContext tournamentOrganizer, Tournament event) {
        LOG.info("Starting next round");
        try {
            var request = new Request.Builder()
                    .post(RequestBody.create(
                            objectMapper.writeValueAsString(""),
                            MediaType.get("application/json")
                    ))
                    .header("Authorization", "Bearer " + tournamentOrganizer.token)
                    .url(String.format("http://localhost:%d/api/v1/tournaments/%s/next-round",
                            port,
                            event.id()))
                    .build();

            try (var response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Unable to go to next round");
                return objectMapper.readValue(response.body().byteStream(), new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}