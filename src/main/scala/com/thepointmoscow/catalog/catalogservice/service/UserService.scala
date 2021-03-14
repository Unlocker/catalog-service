package com.thepointmoscow.catalog.catalogservice.service

import com.thepointmoscow.catalog.catalogservice.domain.User
import com.thepointmoscow.catalog.catalogservice.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.{Flux, Mono}

@Service
@Autowired
class UserService(val userRepository: UserRepository) {

  def createUser(user: User): Mono[User] = userRepository.save(user)

  def getUsers: Flux[User] = userRepository.findAll()

  def findUser(id: Long): Mono[User] = userRepository.findById(id)

  def deleteUser(id: Long): Mono[Void] = userRepository.deleteById(id)
}
