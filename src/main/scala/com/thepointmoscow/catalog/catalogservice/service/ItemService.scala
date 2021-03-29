package com.thepointmoscow.catalog.catalogservice.service

import com.thepointmoscow.catalog.catalogservice.domain.{Item, Selection}
import com.thepointmoscow.catalog.catalogservice.repository.ItemRepository
import com.thepointmoscow.catalog.catalogservice.web.views.ItemInitView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{PageRequest, Sort}
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
@Autowired
class ItemService(private val repo: ItemRepository) {

  val defaultPage: (Option[Int], Option[Int]) => PageRequest = (maybePage, maybeSize) => {
    // UI page enumeration starts with 1 but DB page enumeration start with 0
    val page = maybePage.filter(_ >= 1).map(_ - 1).getOrElse(0)
    val size = maybeSize.filter(_ > 0).getOrElse(50)
    PageRequest.of(page, size, Sort.by("name"))
  }

  def findAll(taxId: String, maybePage: Option[Int], maybeSize: Option[Int]): Mono[Selection[Item]] = {
    val pageInfo = defaultPage(maybePage, maybeSize)
    val totalPages = repo.countByTaxIdentity(taxId).map(count => Math.ceil(count.toDouble / pageInfo.getPageSize).toInt)
    totalPages.map(tp => Selection(repo.findAllByTaxIdentity(taxId, pageInfo), pageInfo.getPageNumber + 1, tp, pageInfo.getPageSize))
  }

  def findByName(taxId: String, name: String, maybePage: Option[Int], maybeSize: Option[Int]): Mono[Selection[Item]] = {
    val pageInfo = defaultPage(maybePage, maybeSize)
    val nameMasked = name
    val totalPages = repo.countByTaxIdentityAndNameContainingIgnoreCase(taxId, nameMasked)
      .map(count => Math.ceil(count.toDouble / pageInfo.getPageSize).toInt)
    totalPages.map(tp => Selection(repo.findAllByTaxIdentityEqualsAndNameContainingIgnoreCase(taxId, nameMasked, pageInfo), pageInfo.getPageNumber + 1, tp, pageInfo.getPageSize))
  }

  def findBySku(taxId: String, sku: String, maybePage: Option[Int], maybeSize: Option[Int]): Mono[Selection[Item]] = {
    val pageInfo = defaultPage(maybePage, maybeSize)
    Mono.just(1).map(tp => Selection(repo.findAllByTaxIdentityEqualsAndSkuEqualsIgnoreCase(taxId, sku, pageInfo), pageInfo.getPageNumber + 1, tp, pageInfo.getPageSize))
  }

  def create(taxId: String, init: ItemInitView): Mono[Item] = {
    val item = new Item()
    item.setName(init.name)
    item.setSku(init.sku.toUpperCase)
    item.setPrice(init.price)
    item.setVatType(init.vatType)
    item.setPaymentObject(init.paymentObject)
    item.setTaxIdentity(taxId)
    repo.save(item)
  }

  def findOne(taxId: String, itemId: Long): Mono[Item] = repo.findById(itemId).filter(_.taxIdentity == taxId)

  def update(taxId: String, itemId: Long, init: ItemInitView): Mono[Item] = {
    findOne(taxId, itemId)
      .map {
        old =>
          old.setName(init.getName)
          old.setSku(init.getSku.toUpperCase)
          old.setPrice(init.getPrice)
          old.setVatType(init.getVatType)
          old.setPaymentObject(init.getPaymentObject)
          old
      }
      .flatMap[Item](repo.save _)
  }

  def remove(taxId: String, itemId: Long): Mono[Void] = findOne(taxId, itemId).flatMap(repo.delete _)

}
