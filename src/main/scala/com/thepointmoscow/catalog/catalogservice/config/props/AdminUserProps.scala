package com.thepointmoscow.catalog.catalogservice.config.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

import java.beans.BeanProperty

@Component
@ConfigurationProperties("ecom-catalog.admin")
class AdminUserProps() {
  @BeanProperty var username: String = _
  @BeanProperty var password: String = _
}
