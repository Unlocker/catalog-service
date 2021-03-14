package com.thepointmoscow.catalog.catalogservice

import com.thepointmoscow.catalog.catalogservice.config.R2dbcConfiguration
import com.thepointmoscow.catalog.catalogservice.domain.User
import com.thepointmoscow.catalog.catalogservice.service.UserService
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.{AfterAll, BeforeAll, BeforeEach, Test}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.{ComponentScan, Configuration, Import}
import org.springframework.context.{ApplicationContext, ApplicationContextInitializer, ConfigurableApplicationContext}
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.{Container, Testcontainers}
import reactor.core.publisher.Hooks
import reactor.test.StepVerifier
import org.springframework.http.MediaType

@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest(
  classes = Array(classOf[TestConfig])
)
@ContextConfiguration(
  initializers = Array(classOf[TestEnvInitializer])
)
class CatalogServiceAppTests() {
  @Autowired var userService: UserService = _
  @Autowired var appContext: ApplicationContext = _

  @BeforeEach
  def setUp(): Unit = {
    Hooks.onOperatorDebug()
    val user = new User(1, "user", "Canada")
    userService.createUser(user)
  }

  @Test
  def testGetUsers(): Unit = {
    userService.getUsers.as(StepVerifier.create(_))
      .thenConsumeWhile(user => user.name == "user")
      .verifyComplete()
  }

  @Test
  def testGetOneItem(): Unit = {
    val webClient = WebTestClient.bindToApplicationContext(appContext).build()
    webClient.get()
      .uri("/api/v1/items/{taxid}", "7708317992")
      .headers(headers => headers.setBasicAuth("admin", "admin"))
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$..sku").isNotEmpty()
  }

}

@Testcontainers
object CatalogServiceAppTests {

  @Container
  var postgreSQLContainer: PostgreSQLContainer[_] = new PostgreSQLContainer() //.withExposedPorts(5432)

  @BeforeAll
  def setUpFixture(): Unit = {
    postgreSQLContainer.start()
  }

  @AfterAll
  def tearDownFixture(): Unit = {
    postgreSQLContainer.stop()
  }
}

@Configuration
@Import(Array(classOf[R2dbcConfiguration]))
@ComponentScan(basePackages = Array())
class TestConfig {

}

class TestEnvInitializer extends ApplicationContextInitializer[ConfigurableApplicationContext] {
  override def initialize(c: ConfigurableApplicationContext): Unit = {
    addInlinedPropertiesToEnvironment(c, "ecom-catalog.datasource.host=localhost")
    addInlinedPropertiesToEnvironment(c, "ecom-catalog.datasource.username=" + CatalogServiceAppTests.postgreSQLContainer.getUsername)
    addInlinedPropertiesToEnvironment(c, "ecom-catalog.datasource.password=" + CatalogServiceAppTests.postgreSQLContainer.getPassword)
    addInlinedPropertiesToEnvironment(c, "ecom-catalog.datasource.database=" + CatalogServiceAppTests.postgreSQLContainer.getDatabaseName)
    addInlinedPropertiesToEnvironment(c, "ecom-catalog.datasource.port=" + CatalogServiceAppTests.postgreSQLContainer.getFirstMappedPort)
  }
}
