package com.thepointmoscow.catalog.catalogservice.config.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

import scala.beans.BeanProperty

@Component
@ConfigurationProperties(prefix = "ecom-catalog.datasource")
class DatabaseProps() {
  @BeanProperty var host: String = _
  @BeanProperty var port: Int = _
  @BeanProperty var database: String = _
  @BeanProperty var username: String = _
  @BeanProperty var password: String = _
}
