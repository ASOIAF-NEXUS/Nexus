package asoiafnexus.database.config;

import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.spring.JdbiFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collection;

@Configuration
public class DatabaseConfiguration {

  @Bean("jdbi")
  public JdbiFactoryBean jdbi(
      DataSource dataSource,
      Collection<JdbiPlugin> plugins
  ) {
    return new JdbiFactoryBean(dataSource)
        .setPlugins(plugins)
        .setAutoInstallPlugins(true);
  }
}
