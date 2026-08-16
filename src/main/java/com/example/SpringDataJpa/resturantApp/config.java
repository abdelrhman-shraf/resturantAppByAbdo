package com.example.SpringDataJpa.resturantApp;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@ComponentScan(basePackages = "com.example.SpringDataJpa.resturantApp")
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.SpringDataJpa.resturantApp",
    entityManagerFactoryRef ="resturantEntityManagerFactory" ,
    transactionManagerRef = "resturantTransaction"

)
public class config {
    @Bean
    @ConfigurationProperties(prefix =  "spring.datasource.resturant")
public DataSourceProperties restaurantDataSourceProperties() {
    return new DataSourceProperties();
}

@Bean(name = "resturantDataSource")
public DataSource restaurantDataSource(
        @Qualifier("restaurantDataSourceProperties")
        DataSourceProperties properties) {

    return properties
            .initializeDataSourceBuilder()
            .build();
}
    @Bean(name = "resturantEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean LCEMF(EntityManagerFactoryBuilder builder,
        @Qualifier("resturantDataSource" ) DataSource dataSource
    ){
        return builder.dataSource(dataSource).packages("com.example.SpringDataJpa.resturantApp").persistenceUnit("resturant").build();

    }
    @Bean(name = "resturantTransaction")
    public PlatformTransactionManager PTM(
        @Qualifier("resturantEntityManagerFactory") EntityManagerFactory EMF){
            return new JpaTransactionManager(EMF);
    }
     /* fronted testing shit  */
     @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false); // set true if you use cookies/authorization
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


}
