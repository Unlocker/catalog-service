package com.thepointmoscow.catalog.catalogservice.web.handler

import com.thepointmoscow.catalog.catalogservice.domain.{Item, Selection}
import com.thepointmoscow.catalog.catalogservice.service.ItemService
import com.thepointmoscow.catalog.catalogservice.web.views.{ItemInitView, ItemView, SelectionView, WebFailure, WebResult, WebSuccess}
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
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
    val result: Mono[Selection[Item]] = if (sku.isPresent) {
      service.findBySku(taxId, sku.get(), maybePage, maybeSize)
    } else if (nameLike.isPresent) {
      service.findByName(taxId, nameLike.get(), maybePage, maybeSize)
    } else {
      service.findAll(taxId, maybePage, maybeSize)
    }
    result
      .flatMap {
        case Selection(itemsFlux, currentPage, totalPages, size) =>
          itemsFlux.map(ItemView.apply)
            .collectList()
            .map(list => WebSuccess(SelectionView[ItemView](list, currentPage, totalPages, size)))
      }
      .flatMap(
        response =>
          ServerResponse.ok()
            .contentType(APPLICATION_JSON)
            .bodyValue(response)
      )
  }

  def create(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val taxId = serverRequest.pathVariable("tax-id")
    val initMono: Mono[ItemInitView] = serverRequest.bodyToMono(classOf[ItemInitView])

    initMono
      .flatMap(
        init => service.create(taxId, init)
          .map[(Int, WebResult[ItemView])](x => 200 -> WebSuccess(ItemView(x)))
          .onErrorReturn(
            (ex: Throwable) => classOf[DataIntegrityViolationException].isInstance(ex)
            , 400 -> WebFailure(400, s"Товар с указанным кодом SKU уже существует")
          )
      )
      .flatMap {
        case (code: Int, body: WebResult[ItemView]) =>
          ServerResponse.status(code).contentType(MediaType.APPLICATION_JSON).bodyValue(body)
      }
  }

  def getOne(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val taxId = serverRequest.pathVariable("tax-id")
    val itemId = serverRequest.pathVariable("item-id").toLong

    val itemMono = service.findOne(taxId, itemId)
    itemMono
      .map(item => WebSuccess(ItemView.apply(item)))
      .flatMap(
        ServerResponse.ok()
          .contentType(APPLICATION_JSON)
          .bodyValue(_)
      )
  }

  def update(serverRequest: ServerRequest): Mono[ServerResponse] = {
    val taxId = serverRequest.pathVariable("tax-id")
    val itemId = serverRequest.pathVariable("item-id").toLong
    val initMono: Mono[ItemInitView] = serverRequest.bodyToMono(classOf[ItemInitView])
    initMono
      .flatMap(service.update(taxId, itemId, _))
      .map(item => WebSuccess(ItemView.apply(item)))
      .flatMap(
        ServerResponse.ok()
          .contentType(APPLICATION_JSON)
          .bodyValue(_)
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