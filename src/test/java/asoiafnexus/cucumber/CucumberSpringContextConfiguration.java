package asoiafnexus.cucumber;

import asoiafnexus.Main;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ContextConfiguration(classes = Main.class, loader = SpringBootContextLoader.class)
public class CucumberSpringContextConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CucumberSpringContextConfiguration.class);

    public static int port;

    @LocalServerPort
    private int portConstructor;

    @Before
    public void setUp() {
        LOG.info("-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
        port = portConstructor;
    }
}
