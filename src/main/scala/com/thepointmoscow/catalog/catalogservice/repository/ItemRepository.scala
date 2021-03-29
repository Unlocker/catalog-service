package com.thepointmoscow.catalog.catalogservice.repository

import com.thepointmoscow.catalog.catalogservice.domain.Item
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.{Flux, Mono}

trait ItemRepository extends ReactiveSortingRepository[Item, Long] {

  def findAllByTaxIdentity(taxIdentity: String, pageRequest: PageRequest): Flux[Item]

  def countByTaxIdentity(taxIdentity: String): Mono[java.lang.Long]

  def findAllByTaxIdentityEqualsAndNameContainingIgnoreCase(taxIdentity: String, name: String, pageRequest: PageRequest): Flux[Item]

  def countByTaxIdentityAndNameContainingIgnoreCase(taxIdentity: String, name: String): Mono[java.lang.Long]

  def findAllByTaxIdentityEqualsAndSkuEqualsIgnoreCase(taxIdentity: String, sku: String, pageRequest: PageRequest): Flux[Item]
}
