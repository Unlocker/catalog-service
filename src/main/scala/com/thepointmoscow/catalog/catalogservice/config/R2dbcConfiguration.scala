package com.thepointmoscow.catalog.catalogservice.config

import com.thepointmoscow.catalog.catalogservice.config.props.DatabaseProps
import io.r2dbc.postgresql.{PostgresqlConnectionConfiguration, PostgresqlConnectionFactory}
import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

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

}
