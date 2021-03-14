package com.thepointmoscow.catalog.catalogservice.web.views

import com.thepointmoscow.catalog.catalogservice.domain.Item

import java.beans.BeanProperty

class ItemView() {
  @BeanProperty var itemId: Long = _
  @BeanProperty var name: String = _
  @BeanProperty var sku: String = _
  @BeanProperty var price: BigDecimal = _
  @BeanProperty var vatType: String = _
  @BeanProperty var paymentObject: String = _
  @BeanProperty var taxIdentity: String = _
}

object ItemView {

  def apply(i: Item): ItemView = {
    val x = new ItemView()
    x.setItemId(i.getItemId)
    x.setName(i.getName)
    x.setSku(i.getSku)
    x.setPrice(i.getPrice)
    x.setVatType(i.getVatType)
    x.setPaymentObject(i.getPaymentObject)
    x.setTaxIdentity(i.getTaxIdentity)
    x
  }

}
