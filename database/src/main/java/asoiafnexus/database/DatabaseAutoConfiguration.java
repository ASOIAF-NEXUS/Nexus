package asoiafnexus.db;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.jackson2.Jackson2Config;
import org.jdbi.v3.spring.JdbiFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collection;

@AutoConfiguration
public class DatabaseAutoConfiguration {

  @Bean("jdbi")
  public JdbiFactoryBean jdbi(
      DataSource dataSource,
      Collection<JdbiPlugin> plugins
  ) {
    return new JdbiFactoryBean(dataSource)
        .setPlugins(plugins)
        .setAutoInstallPlugins(true);
  }

  @Configuration(proxyBeanMethods = false)
  public static class JdbiObjectMapperConfiguration {

    @Autowired
    public void customizeJdbiObjectMapper(Jdbi jdbi) {
      jdbi.getConfig(Jackson2Config.class)
          .getMapper()
          .findAndRegisterModules()
          .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
  }
}
