package com.thepointmoscow.catalog.catalogservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.{Column, Table}

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Table("items")
class Item() {
  @(Id@field)
  @BeanProperty
  @Column("item_id") var itemId: Long = _
  @BeanProperty
  @Column("name") var name: String = _
  @BeanProperty
  @Column("sku") var sku: String = _
  @BeanProperty
  @Column("price") var price: Double = _
  @BeanProperty
  @Column("vat_type") var vatType: String = _
  @BeanProperty
  @Column("payment_object") var paymentObject: String = _
  @BeanProperty
  @Column("tax_identity") var taxIdentity: String = _

}
