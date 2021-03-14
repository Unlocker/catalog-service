package com.thepointmoscow.catalog.catalogservice.config

import com.thepointmoscow.catalog.catalogservice.web.handler.{ItemHandler, UserHandler}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.{RouterFunction, RouterFunctions, ServerResponse}

@Configuration
@EnableWebFlux
class RouterConfiguration {

  @Bean
  def routerFunction(userHandler: UserHandler, itemHandler: ItemHandler): RouterFunction[ServerResponse] = {
    RouterFunctions.route()
      // users API
      .POST("/users", userHandler.createUser)
      .GET("/users", userHandler.getUsers)
      .GET("/users/{id}", userHandler.getUser)
      .DELETE("/user/{id}", userHandler.deleteUser)
      // items API
      .POST("/api/v1/items/{tax-id}", itemHandler.create)
      .GET("/api/v1/items/{tax-id}", itemHandler.getAll)
      .GET("/api/v1/items/{tax-id}/{item-id}", itemHandler.getOne)
      .PUT("/api/v1/items/{tax-id}/{item-id}", itemHandler.update)
      .DELETE("/api/v1/items/{tax-id}/{item-id}", itemHandler.remove)
      .build()
  }

}
