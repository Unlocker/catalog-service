package com.thepointmoscow.catalog.catalogservice

import com.thepointmoscow.catalog.catalogservice.config.R2dbcConfiguration
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
import org.springframework.web.reactive.function.client.{ExchangeFunction, WebClient}

@ExtendWith(Array(classOf[SpringExtension]))
@SpringBootTest(
  classes = Array(classOf[TestConfig])
)
@ContextConfiguration(
  initializers = Array(classOf[TestEnvInitializer])
)
class CatalogServiceAppTests() {
  @Autowired var appContext: ApplicationContext = _

  var webClient: WebTestClient = _

  @BeforeEach
  def setUp(): Unit = {
    webClient = WebTestClient.bindToApplicationContext(appContext).build()
    Hooks.onOperatorDebug()
  }

  @Test
  def testGetItemsForTaxId(): Unit = {
    webClient.get()
      .uri("/api/v1/items/{taxid}", "7708317992")
      .headers(_.setBasicAuth("admin", "admin"))
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.errorCode").isEqualTo(0)
      .jsonPath("$.payload.currentPage").isEqualTo(1)
      .jsonPath("$.payload.totalPages").isEqualTo(1)
      .jsonPath("$.payload.size").isEqualTo(50)
      .jsonPath("$.payload.items").isNotEmpty()
  }

  @Test
  def testGetOneItemForSku(): Unit = {
    webClient.get()
      .uri("/api/v1/items/{taxid}?sku={sku}", "7708317992", "????-36")
      .headers(_.setBasicAuth("admin", "admin"))
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.errorCode").isEqualTo(0)
      .jsonPath("$.payload").isNotEmpty()
      .jsonPath("$.payload.currentPage").isEqualTo(1)
      .jsonPath("$.payload.totalPages").isEqualTo(1)
      .jsonPath("$.payload.size").isEqualTo(50)
      .jsonPath("$.payload.items[0].sku").isEqualTo("????-36")
  }

  @Test
  def testDuplicateItem(): Unit = {
    webClient.post()
      .uri("/api/v1/items/{taxid}", "7708317992")
      .headers(_.setBasicAuth("admin", "admin"))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""{"name":"FN-36","sku":"????-36","price":100.00}""")
      .exchange()
      .expectStatus().isBadRequest()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.errorCode").isEqualTo(400)
      .jsonPath("$.payload").isEmpty()
      .jsonPath("$.error").isEqualTo("?????????? ?? ?????????????????? ?????????? SKU ?????? ????????????????????")
  }

  @Test
  def testCreateItem(): Unit = {
    webClient.post()
      .uri("/api/v1/items/{taxid}", "7708317992")
      .headers(_.setBasicAuth("admin", "admin"))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue("""{"name":"???????????????? ????????????","sku":"SERV-01","price":100.00, "vatType": "vat20", "paymentObject": "service"}""")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.errorCode").isEqualTo(0)
      .jsonPath("$.payload").isNotEmpty()
      .jsonPath("$.payload.name").isEqualTo("???????????????? ????????????")
      .jsonPath("$.payload.sku").isEqualTo("SERV-01")
      .jsonPath("$.payload.price").isEqualTo(100.00)
      .jsonPath("$.payload.vatType").isEqualTo("vat20")
      .jsonPath("$.payload.paymentObject").isEqualTo("service")
  }

  @Test
  def testGetItemsByName(): Unit = {
    webClient.get()
      .uri("/api/v1/items/{taxid}?name={name}", "7708317992", "????????????????????")
      .headers(_.setBasicAuth("admin", "admin"))
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.errorCode").isEqualTo(0)
      .jsonPath("$.payload").isNotEmpty()
      .jsonPath("$.payload.items").isNotEmpty()
      .jsonPath("$.payload.currentPage").isEqualTo(1)
      .jsonPath("$.payload.totalPages").isEqualTo(1)
      .jsonPath("$.payload.size").isEqualTo(50)
  }

  @Test
  def testGetItemById(): Unit = {
    webClient.get()
      .uri("/api/v1/items/{taxid}/{itemId}", "7708317992", 894)
      .headers(_.setBasicAuth("admin", "admin"))
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.errorCode").isEqualTo(0)
      .jsonPath("$.payload").isNotEmpty()
      .jsonPath("$.payload.itemId").isEqualTo(894)
      .jsonPath("$.payload.taxIdentity").isEqualTo("7708317992")
      .jsonPath("$.payload.name").isEqualTo("???????????????????? ???????????????????? 36 ??????????????")
      .jsonPath("$.payload.sku").isEqualTo("????-36")
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
