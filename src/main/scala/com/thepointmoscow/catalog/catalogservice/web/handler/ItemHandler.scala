package com.thepointmoscow.catalog.catalogservice.web.handler

import com.thepointmoscow.catalog.catalogservice.service.ItemService
import com.thepointmoscow.catalog.catalogservice.web.views.{ItemInitView, ItemView}
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.{ServerRequest, ServerResponse}
import reactor.core.publisher.Mono

import scala.jdk.javaapi.OptionConverters.toScala


@Component
class ItemHandler(service: ItemService) {

  def getAll(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val maybePage = toScala(serverRequest.queryParam("page")).flatMap(_.toIntOption)
    val maybeSize = toScala(serverRequest.queryParam("size")).flatMap(_.toIntOption)
    val taxId = serverRequest.pathVariable("tax-id")
    val sku = serverRequest.queryParam("sku")
    val nameLike = serverRequest.queryParam("name")
    val result = if (sku.isPresent) {
      service.findBySku(taxId, sku.get(), maybePage, maybeSize)
    } else if (nameLike.isPresent) {
      service.findByName(taxId, nameLike.get(), maybePage, maybeSize)
    } else {
      service.findAll(taxId, maybePage, maybeSize)
    }
    val (items, _, _) = result
    items.map(ItemView.apply).collectList().flatMap(
      ServerResponse.ok()
        .contentType(APPLICATION_JSON)
        .bodyValue(_)
    )
  }

  def create(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val taxId = serverRequest.pathVariable("tax-id")
    val initMono: Mono[ItemInitView] = serverRequest.bodyToMono(classOf[ItemInitView])

    initMono
      .flatMap(init => service.create(taxId, init))
      .flatMap(
        item => ServerResponse.ok()
          .contentType(APPLICATION_JSON)
          .body(ItemView.apply(item), classOf[ItemView])
      )
  }

  def getOne(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val taxId = serverRequest.pathVariable("tax-id")
    val itemId = serverRequest.pathVariable("item-id").toLong

    val itemMono = service.findOne(taxId, itemId)
    itemMono.map(ItemView.apply).flatMap(
      ServerResponse.ok()
        .contentType(APPLICATION_JSON)
        .body(_, classOf[ItemView])
    )
  }

  def update(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val taxId = serverRequest.pathVariable("tax-id")
    val itemId = serverRequest.pathVariable("item-id").toLong
    val initMono: Mono[ItemInitView] = serverRequest.bodyToMono(classOf[ItemInitView])
    initMono
      .flatMap(service.update(taxId, itemId, _))
      .map(ItemView.apply)
      .flatMap(
        ServerResponse.ok()
          .contentType(APPLICATION_JSON)
          .body(_, classOf[ItemView])
      )
  }

  def remove(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val taxId = serverRequest.pathVariable("tax-id")
    val itemId = serverRequest.pathVariable("item-id").toLong
    service.remove(taxId, itemId).flatMap(
      _ => ServerResponse.noContent().build()
    )
  }
}