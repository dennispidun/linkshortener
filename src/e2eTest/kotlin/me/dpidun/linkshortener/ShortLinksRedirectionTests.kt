package me.dpidun.linkshortener

import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import io.restassured.parsing.Parser
import me.dpidun.linkshortener.dao.ShortLinkDao
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ShortLinksRedirectionTests(@LocalServerPort val port: Int) {

    @Autowired
    lateinit var shortLinkRepository: ShortLinkRepository

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        defaultParser = Parser.JSON
    }

    @Test
    fun `should create a shortlink`() {
        val createdShortLink = createShortLink()

        val shortLinks: List<ShortLinkDao> = get("/shortlinks").then()
            .extract()
            .body().jsonPath().getList("shortLinks", ShortLinkDao::class.java)

        assertThat(shortLinks).contains(createdShortLink)

        val foundShortLink: ShortLinkDao = get("/shortlinks/${createdShortLink?.id!!}").then()
            .extract()
            .`as`(ShortLinkDao::class.java)

        assertThat(foundShortLink).isEqualTo(createdShortLink)

        val repoShortLink = shortLinkRepository.findById(createdShortLink.id!!).get()

        assertThat(repoShortLink).isEqualTo(createdShortLink)
    }

    @Test
    fun `should create a shortlink which redirects and gets deleted afterwards`() {
        val createdShortLink = createShortLink()

        given()
            .contentType(ContentType.JSON)
            .redirects().follow(false)
            .get("/${createdShortLink?.hash}")
            .then()
            .statusCode(302)
            .headers(
                "Location", equalTo(createdShortLink?.redirectUrl)
            )

        delete("/shortlinks/${createdShortLink?.id}").then()

        given()
            .contentType(ContentType.JSON)
            .redirects().follow(false)
            .get("/${createdShortLink?.hash}")
            .then()
            .statusCode(404)

        val repoShortLink = shortLinkRepository.findById(createdShortLink?.id!!)

        assertTrue(repoShortLink.isEmpty)
    }


    @Test
    fun `should create a shortlink which redirects and gets updated afterwards`() {
        val createdShortLink = createShortLink()

        given()
            .contentType(ContentType.JSON)
            .redirects().follow(false)
            .get("/${createdShortLink?.hash}")
            .then()
            .statusCode(302)
            .headers(
                "Location", equalTo(createdShortLink?.redirectUrl)
            )

        val newUrl = "https://something.org"

        given().contentType(ContentType.JSON).body(
            """
                    {
                        "redirectUrl": "$newUrl"
                    }
                """.trimIndent()
        ).put("/shortlinks/${createdShortLink?.id}").then()

        given()
            .contentType(ContentType.JSON)
            .redirects().follow(false)
            .get("/${createdShortLink?.hash}")
            .then()
            .statusCode(302)
            .headers(
                "Location", equalTo(newUrl)
            )

        val repoShortLink = shortLinkRepository.findById(createdShortLink?.id!!).get()

        assertEquals(repoShortLink.redirectUrl, newUrl)
    }

    private fun createShortLink(): ShortLinkDao? = given().contentType(ContentType.JSON).body(
        """
                    {
                        "name": "shortlink",
                        "redirectUrl": "https://wikipedia.org"
                    }
                """.trimIndent()
    ).post("/shortlinks").then().extract().`as`(ShortLinkDao::class.java)


}
