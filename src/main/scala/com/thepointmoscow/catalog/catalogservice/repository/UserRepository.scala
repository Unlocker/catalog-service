package com.thepointmoscow.catalog.catalogservice.repository

import com.thepointmoscow.catalog.catalogservice.domain.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository

trait UserRepository extends ReactiveCrudRepository[User, Long] {

}
