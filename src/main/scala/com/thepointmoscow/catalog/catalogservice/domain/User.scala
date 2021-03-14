package com.thepointmoscow.catalog.catalogservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Table("users")
class User(
            @(Id@field) @BeanProperty var id: Long
            , @BeanProperty var name: String
            , @BeanProperty var country: String
          ) {
  def this() = this(-1, null, null)
}
