package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataBaseConfig {

    @Bean(name = "albumDataSource")
    @ConfigurationProperties("moviefun.datasources.movies")
    public DataSource moviesDataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "moviesDataSource")
    @ConfigurationProperties("moviefun.datasources.albums")
    public DataSource albumsDataSource() {
        return DataSourceBuilder.create().build();
    }

    public HikariDataSource hikariDataSource (DataSource dataSource){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(dataSource);
        hikariConfig.setPoolName("movies-fun-pool");
        hikariConfig.setMaximumPoolSize(3);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
       HibernateJpaVendorAdapter hibernateJpaVendorAdapter= new HibernateJpaVendorAdapter();
       hibernateJpaVendorAdapter.setShowSql(true);
       hibernateJpaVendorAdapter.setGenerateDdl(true);
       hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
       hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");

       return hibernateJpaVendorAdapter;
    }

    @Bean(name = "moviesEntityManager")
    public LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean(@Qualifier("moviesDataSource") DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanMovies = new LocalContainerEntityManagerFactoryBean();
        //localContainerEntityManagerFactoryBeanMovies.setDataSource(dataSource);
        localContainerEntityManagerFactoryBeanMovies.setDataSource(hikariDataSource(dataSource));
        localContainerEntityManagerFactoryBeanMovies.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBeanMovies.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBeanMovies.setPersistenceUnitName("movies-unit");
        return localContainerEntityManagerFactoryBeanMovies;
    }

    @Bean(name = "albumsEntityManager")
    public LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean(@Qualifier("albumDataSource") DataSource dataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBeanAlbums = new LocalContainerEntityManagerFactoryBean();
        //localContainerEntityManagerFactoryBeanAlbums.setDataSource(dataSource);
        localContainerEntityManagerFactoryBeanAlbums.setDataSource(hikariDataSource(dataSource));
        localContainerEntityManagerFactoryBeanAlbums.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryBeanAlbums.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBeanAlbums.setPersistenceUnitName("albums-unit");
        return localContainerEntityManagerFactoryBeanAlbums;
    }


    @Bean(name = "moviesTransactionManager")
    public PlatformTransactionManager moviesPlatformTransactionManager(@Qualifier("moviesEntityManager") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManagerMovies = new JpaTransactionManager();
        jpaTransactionManagerMovies.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return jpaTransactionManagerMovies;
    }


    @Bean(name = "albumsTransactionManager")
    public PlatformTransactionManager albumsPlatformTransactionManager(@Qualifier("albumsEntityManager") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManagerAlbum = new JpaTransactionManager();
        jpaTransactionManagerAlbum.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return jpaTransactionManagerAlbum;
    }
}
