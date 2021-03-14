package com.thepointmoscow.catalog.catalogservice.config

import com.thepointmoscow.catalog.catalogservice.config.props.AdminUserProps
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.{MapReactiveUserDetailsService, User, UserDetails}
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
class SecurityConfiguration {

  @Bean
  def userDetailsService(adminUserProps: AdminUserProps): MapReactiveUserDetailsService = {
    val admin: UserDetails = User.withDefaultPasswordEncoder()
      .username(adminUserProps.username)
      .password(adminUserProps.password)
      .roles("ADMIN")
      .build()
    new MapReactiveUserDetailsService(admin)
  }

  @Bean
  def springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = {
    http
      .authorizeExchange()
      .pathMatchers("/api/v1/items/**").hasRole("ADMIN")
      .anyExchange().permitAll()
      .and()
      .httpBasic()
    http.build()
  }

}
