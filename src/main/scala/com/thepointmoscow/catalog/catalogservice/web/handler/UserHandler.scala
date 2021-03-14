package com.thepointmoscow.catalog.catalogservice.web.handler

import com.thepointmoscow.catalog.catalogservice.domain.User
import com.thepointmoscow.catalog.catalogservice.service.UserService
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.{ServerRequest, ServerResponse}
import reactor.core.publisher.Mono

@Component
class UserHandler(val userService: UserService) {

  def createUser(serverRequest: ServerRequest): Mono[ServerResponse] = {
    serverRequest.bodyToMono(classOf[User])
      .flatMap(userService.createUser)
      .flatMap(u => ServerResponse.status(CREATED)
        .contentType(APPLICATION_JSON)
        .body(u, classOf[User])
      )
  }

  def getUsers(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val okResp: Mono[ServerResponse] = userService.getUsers.collectList()
      .flatMap(
        ServerResponse.ok()
          .contentType(APPLICATION_JSON)
          .body(_, classOf[List[User]])
      )
    okResp.switchIfEmpty(ServerResponse.noContent().build())
  }

  def getUser(serverRequest: ServerRequest): Mono[ServerResponse] = {
    userService.findUser(serverRequest.pathVariable("id").toLong)
      .flatMap[ServerResponse](
        (u: User) =>
          ServerResponse
            .ok()
            .contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(u))
      )
      .switchIfEmpty(ServerResponse.notFound().build())
  }

  def deleteUser(serverRequest: ServerRequest): Mono[ServerResponse] = {
    userService.deleteUser(serverRequest.pathVariable("id").toLong)
      .flatMap(_ => ServerResponse.ok().contentType(APPLICATION_JSON).build())
  }

}
