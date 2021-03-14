package com.thepointmoscow.catalog.catalogservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class CatalogServiceApp {
}

object CatalogServiceApp extends App {
  SpringApplication.run(classOf[CatalogServiceApp], args: _*)
}
