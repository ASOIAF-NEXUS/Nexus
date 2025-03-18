package asoiafnexus.cucumber;

import asoiafnexus.tournament.model.Pairing;
import asoiafnexus.tournament.model.Player;
import asoiafnexus.tournament.model.Tournament;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class NexusClient {
    private static final Logger LOG = LoggerFactory.getLogger(NexusClient.class);

    private OkHttpClient client = new OkHttpClient();

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public void createTournament(Tournament t, Consumer<Response> responseHandler) throws IOException {
        var request = new Request.Builder()
                .post(RequestBody.create(
                        objectMapper.writeValueAsString(t),
                        MediaType.get("application/json")))
                .url(String.format("http://localhost:%d/api/v1/tournaments", CucumberSpringContextConfiguration.port))
                .build();

        try(var response = client.newCall(request).execute()) {
            responseHandler.accept(response);
        }
    }

    public void updateTournament(Tournament t, Consumer<Response> responseHandler) throws IOException {
        var request = new Request.Builder()
                .put(RequestBody.create(
                        objectMapper.writeValueAsString(t),
                        MediaType.get("application/json")))
                .url(String.format("http://localhost:%d/api/v1/tournaments/%s",
                        CucumberSpringContextConfiguration.port,
                        t.id()))
                .build();

        try(var response = client.newCall(request).execute()) {
            responseHandler.accept(response);
        }
    }

    public void tournamentSignup(Tournament t, Player p, Consumer<Response> responseHandler) throws IOException {
        var request = new Request.Builder()
                .post(RequestBody.create(
                        objectMapper.writeValueAsString(p),
                        MediaType.get("application/json")))
                .url(String.format("http://localhost:%d/api/v1/tournaments/%s/register",
                        CucumberSpringContextConfiguration.port,
                        t.id()))
                .build();

        try(var response = client.newCall(request).execute()) {
            responseHandler.accept(response);
        }
    }

    public void tournamentWithdraw(Tournament t, Player p, Consumer<Response> responseHandler) throws IOException {
        var request = new Request.Builder()
                .post(RequestBody.create(
                        objectMapper.writeValueAsString(p),
                        MediaType.get("application/json")))
                .url(String.format("http://localhost:%d/api/v1/tournaments/%s/withdraw",
                        CucumberSpringContextConfiguration.port,
                        t.id()))
                .build();

        try(var response = client.newCall(request).execute()) {
            responseHandler.accept(response);
        }
    }

    public void tournamentStart(Tournament t, Consumer<Response> responseHandler) throws IOException {
        var request = new Request.Builder()
                .post(RequestBody.create(
                        objectMapper.writeValueAsString(""),
                        MediaType.get("application/json")
                ))
                .url(String.format("http://localhost:%d/api/v1/tournaments/%s/start",
                        CucumberSpringContextConfiguration.port,
                        t.id()))
                .build();

        try(var response = client.newCall(request).execute()) {
            responseHandler.accept(response);
        }
    }

    public void tournamentSetPairings(Tournament t, List<Pairing> pairings, Consumer<Response> responseHandler) throws IOException {
        var request = new Request.Builder()
                .put(RequestBody.create(
                        objectMapper.writeValueAsString(pairings),
                        MediaType.get("application/json")
                ))
                .url(String.format("http://localhost:%d/api/v1/tournaments/%s/pairings",
                        CucumberSpringContextConfiguration.port,
                        t.id()))
                .build();

        try(var response = client.newCall(request).execute()) {
            responseHandler.accept(response);
        }
    }

    public List<Tournament> getAllTournaments() throws IOException {
        var request = new Request.Builder()
                .url(String.format("http://localhost:%d/api/v1/tournaments", CucumberSpringContextConfiguration.port))
                .get()
                .build();

        try(var response = client.newCall(request).execute()) {
            return objectMapper.readValue(response.body().byteStream(), new TypeReference<>() {});
        }
    }
}
