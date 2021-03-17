package com.thepointmoscow.catalog.catalogservice.repository

import com.thepointmoscow.catalog.catalogservice.domain.Item
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.{Flux, Mono}

trait ItemRepository extends ReactiveSortingRepository[Item, Long] {

  def findAllByTaxIdentity(taxIdentity: String, pageRequest: PageRequest): Flux[Item]

  def countByTaxIdentity(taxIdentity: String): Mono[java.lang.Long]

  def findAllByTaxIdentityEqualsAndNameIsLike(taxIdentity: String, name: String, pageRequest: PageRequest): Flux[Item]

  def countByTaxIdentityAndNameIsLike(taxIdentity: String, name: String): Mono[java.lang.Long]

  def findAllByTaxIdentityEqualsAndSkuEquals(taxIdentity: String, sku: String, pageRequest: PageRequest): Flux[Item]
}
