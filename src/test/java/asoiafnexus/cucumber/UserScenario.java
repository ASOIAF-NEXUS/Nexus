package asoiafnexus.cucumber;

import asoiafnexus.user.model.Login;
import asoiafnexus.user.model.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Step Definitions for actions relating to the {@link User} Resource
 */
public class UserScenario {
    private static final Logger LOG = LoggerFactory.getLogger(UserScenario.class);

    NexusClient client = NexusClient.instance;



    @Given("The following users have signed up")
    public void signupUser(DataTable users) {
        users.asMaps().forEach(x -> {
            try {
                client.signupUser(x.get("username"), r -> {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Given("a user {string}")
    public void loginUser(String username) throws IOException {
        client.withToken(client.loginUser(username));
    }

}
