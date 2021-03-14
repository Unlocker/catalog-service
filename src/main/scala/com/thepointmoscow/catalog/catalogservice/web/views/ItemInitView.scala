package com.thepointmoscow.catalog.catalogservice.web.views

import scala.beans.BeanProperty

class ItemInitView() {
  @BeanProperty var name: String = _
  @BeanProperty var sku: String = _
  @BeanProperty var price: Double = _
  @BeanProperty var vatType: String = _
  @BeanProperty var paymentObject: String = _
}
