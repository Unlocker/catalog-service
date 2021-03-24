package com.thepointmoscow.catalog.catalogservice.web.views

import java.beans.BeanProperty
import java.util.{List => JavaList}

class SelectionView[T]() {
  @BeanProperty var items: JavaList[T] = _
  @BeanProperty var currentPage: Int = _
  @BeanProperty var totalPages: Int = _
  @BeanProperty var size: Int = _
}

object SelectionView {
  def apply[T](items: JavaList[T], currentPage: Int, totalPages: Int, size: Int): SelectionView[T] = {
    val sel = new SelectionView[T]()
    sel.items = items
    sel.currentPage = currentPage
    sel.totalPages = totalPages
    sel.size = size
    sel
  }
}
