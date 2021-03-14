package com.thepointmoscow.catalog.catalogservice.config

import com.thepointmoscow.catalog.catalogservice.config.props.DatabaseProps
import com.thepointmoscow.catalog.catalogservice.domain.User
import com.thepointmoscow.catalog.catalogservice.repository.UserRepository
import io.r2dbc.postgresql.{PostgresqlConnectionConfiguration, PostgresqlConnectionFactory}
import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient

import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties
@EnableR2dbcRepositories(basePackages = Array("com.thepointmoscow.catalog.catalogservice.repository"))
class R2dbcConfiguration(
                          dbProps: DatabaseProps
                        )
  extends AbstractR2dbcConfiguration() {

  @Bean
  override def connectionFactory: PostgresqlConnectionFactory = {
    new PostgresqlConnectionFactory(
      PostgresqlConnectionConfiguration.builder
        .host(dbProps.getHost)
        .database(dbProps.getDatabase)
        .username(dbProps.getUsername)
        .password(dbProps.getPassword)
        .port(dbProps.getPort)
        .build
    )
  }

  @Bean
  def dataSource: DataSource = {
    val ds = new org.postgresql.ds.PGSimpleDataSource
    ds.setServerNames(Array(dbProps.getHost))
    ds.setPortNumbers(Array(dbProps.getPort))
    ds.setDatabaseName(dbProps.getDatabase)
    ds.setUser(dbProps.getUsername)
    ds.setPassword(dbProps.getPassword)
    ds
  }


  @Bean
  def liquibase(dataSource: DataSource): SpringLiquibase = {
    val liquibase = new SpringLiquibase
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml")
    liquibase.setDataSource(dataSource)
    liquibase
  }

  @Bean
  def commandLineRunner(userRepository: UserRepository): CommandLineRunner = (args: Array[String]) => {
    (args: Array[String]) => {

      val databaseClient = DatabaseClient.create(connectionFactory)
      List(
        "DROP TABLE IF EXISTS users;"
        , "CREATE TABLE users (id serial primary key, name varchar, country varchar);"
      ).foreach(databaseClient.sql(_).fetch.rowsUpdated.block)
      userRepository.save(new User(1, "test", "USA")).log.subscribe
      userRepository.findAll.log().subscribe((u: User) => System.out.println(u))
    }
  }
}
