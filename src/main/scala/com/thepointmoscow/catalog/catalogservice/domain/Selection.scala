package com.thepointmoscow.catalog.catalogservice.domain

import reactor.core.publisher.Flux

case class Selection[T](items: Flux[T], currentPage: Int, totalPages: Int, size: Int)
