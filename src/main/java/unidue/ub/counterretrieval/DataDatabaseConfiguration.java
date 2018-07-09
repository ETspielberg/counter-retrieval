package unidue.ub.counterretrieval;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "dataEntityManagerFactory",
        transactionManagerRef = "dataTransactionManager",
        basePackages = { "unidue.ub.counterretrieval.datarepositories" }
)
public class DataDatabaseConfiguration {

    @Bean(name = "dataDataSource")
    @ConfigurationProperties(prefix = "spring.data.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dataEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean
    dataEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataDataSource") DataSource dataSource
    ) {
        return
                builder
                        .dataSource(dataSource)
                        .packages("unidue.ub.counterretrieval.model.data")
                        .persistenceUnit("ebookcounter")
                        .persistenceUnit("databasecounter")
                        .persistenceUnit("journalcounter")
                        .build();
    }

    @Bean(name = "dataTransactionManager")
    public PlatformTransactionManager dataTransactionManager(
            @Qualifier("dataEntityManagerFactory") EntityManagerFactory
                    barEntityManagerFactory
    ) {
        return new JpaTransactionManager(barEntityManagerFactory);
    }
}
