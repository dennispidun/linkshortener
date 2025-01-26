package me.dpidun.linkshortener.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import io.swagger.v3.core.util.Json
import me.dpidun.linkshortener.dao.ShortLinkDao
import me.dpidun.linkshortener.dto.CreateShortLinkDto
import me.dpidun.linkshortener.dto.UpdateShortLinkDto
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.hamcrest.Matchers
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*
import kotlin.test.assertEquals

class ShortLinkControllerTests {

    @Nested
    @WebMvcTest
    @AutoConfigureMockMvc
    inner class CreateShortLink(@Autowired val mockMvc: MockMvc) {

        @MockkBean
        private lateinit var shortLinkRepository: ShortLinkRepository

        private val mapper = Json.mapper()

        @Test
        fun `should create ShortLink`() {
            val name = "someName"
            val redirectUrl = "https://example.com"
            val hash = "abcdefghi"

            every { shortLinkRepository.save(any()) } returns ShortLinkDao(
                1,
                name,
                redirectUrl,
                hash
            )

            mockMvc.perform(
                MockMvcRequestBuilders.post("/shortlinks").content(
                    mapper.writeValueAsString(CreateShortLinkDto(name = name, redirectUrl = redirectUrl))
                        .trimIndent()
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo(name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.redirectUrl", Matchers.equalTo(redirectUrl)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hash", Matchers.equalTo(hash)))

            verify {
                shortLinkRepository.save(withArg {
                    assertEquals(it.name, name)
                    assertEquals(it.redirectUrl, redirectUrl)
                })
            }
        }

        @ParameterizedTest(name = "should return BadRequest if name is: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = ["some super long name, indicating that it is invalid", "a"])
        fun `should return BadRequest on create if name is invalid`(name: String?) {
            val redirectUrl = "https://example.com/"

            mockMvc.perform(
                MockMvcRequestBuilders.post("/shortlinks").content(
                    mapper.writeValueAsString(CreateShortLinkDto(name = name, redirectUrl = redirectUrl))
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

            verify(exactly = 0) { shortLinkRepository.save(any()) }
        }

        @ParameterizedTest(name = "should return BadRequest if redirectUrl is: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = ["https:not-valid-domain-with-protocol"])
        fun `should return BadRequest on create if redirectUrl is invalid`(redirectUrl: String?) {
            val name = "someName"

            mockMvc.perform(
                MockMvcRequestBuilders.post("/shortlinks").content(
                    mapper.writeValueAsString(CreateShortLinkDto(name, redirectUrl))
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

            verify(exactly = 0) { shortLinkRepository.save(any()) }
        }

        @Test
        fun `should return BadRequest on create for missing redirect url`() {
            val name = "someName"

            mockMvc.perform(
                MockMvcRequestBuilders.post("/shortlinks").content(
                    mapper.writeValueAsString(mapOf("name" to name))
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

            verify(exactly = 0) { shortLinkRepository.save(any()) }
        }

        @Test
        fun `should return BadRequest on create for missing name`() {
            val redirectUrl = "https://example.com"

            mockMvc.perform(
                MockMvcRequestBuilders.post("/shortlinks").content(
                    mapper.writeValueAsString(mapOf("redirectUrl" to redirectUrl))
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

            verify(exactly = 0) { shortLinkRepository.save(any()) }
        }

        @Test
        fun `should return BadRequest on create for malformed body`() {
            mockMvc.perform(
                MockMvcRequestBuilders.post("/shortlinks").content(
                    """
            {
                "some_not" soValid, "Json
            }
        """.trimIndent()
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

            verify(exactly = 0) { shortLinkRepository.save(any()) }
        }
    }

    @Nested
    @WebMvcTest
    @AutoConfigureMockMvc
    inner class GetShortLinks(@Autowired val mockMvc: MockMvc) {

        @MockkBean
        private lateinit var shortLinkRepository: ShortLinkRepository

        @Test
        fun `should return two ShortLinks`() {
            val redirectUrl1 = "https://github.com"
            val redirectUrl2 = "https://example.com"
            val hash1 = "abcdefghi"
            val hash2 = "abcdefghi"
            val name1 = "link1"
            val name2 = "link2"

            every { shortLinkRepository.findAll() } returns listOf(
                ShortLinkDao(1, name1, redirectUrl1, hash1),
                ShortLinkDao(2, name2, redirectUrl2, hash2)

            )

            mockMvc.perform(
                MockMvcRequestBuilders.get("/shortlinks").contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortLinks", Matchers.hasSize<Any>(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortLinks[0].id", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortLinks[0].name", Matchers.equalTo(name1)))
                .andExpect(
                    MockMvcResultMatchers.jsonPath(
                        "$.shortLinks[0].redirectUrl",
                        Matchers.equalTo(redirectUrl1)
                    )
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortLinks[0].hash", Matchers.equalTo(hash1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortLinks[1].id", Matchers.equalTo(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortLinks[1].name", Matchers.equalTo(name2)))
                .andExpect(
                    MockMvcResultMatchers.jsonPath(
                        "$.shortLinks[1].redirectUrl",
                        Matchers.equalTo(redirectUrl2)
                    )
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortLinks[1].hash", Matchers.equalTo(hash2)))
        }

        @Test
        fun `should return one ShortLink`() {
            val redirectUrl = "https://github.com"
            val hash = "abcdefghi"
            val name = "link1"

            every { shortLinkRepository.findById(1) } returns Optional.of(
                ShortLinkDao(1, name, redirectUrl, hash)
            )

            mockMvc.perform(
                MockMvcRequestBuilders.get("/shortlinks/1").contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo(name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.redirectUrl", Matchers.equalTo(redirectUrl)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hash", Matchers.equalTo(hash)))
        }

        @Test
        fun `should return NotFound if ShortLink is not available`() {
            every { shortLinkRepository.findById(1) } returns Optional.empty()

            mockMvc.perform(
                MockMvcRequestBuilders.get("/shortlinks/1").contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }

    @Nested
    @WebMvcTest
    @AutoConfigureMockMvc
    inner class UpdateShortLink(@Autowired val mockMvc: MockMvc) {

        @MockkBean
        private lateinit var shortLinkRepository: ShortLinkRepository

        private val mapper = Json.mapper()

        @Test
        fun `should update ShortLink name`() {
            val newName = "someNewName"
            val redirectUrl = "https://example.com"
            val hash = "abcdefghi"

            every { shortLinkRepository.save(any()) } returns ShortLinkDao(
                1,
                newName,
                redirectUrl,
                hash
            )

            every { shortLinkRepository.findById(1) } returns Optional.of(ShortLinkDao(1, "oldName", redirectUrl, hash))

            mockMvc.perform(
                MockMvcRequestBuilders.put("/shortlinks/1").content(
                    """
            {
                "name": "$newName"
            }
        """.trimIndent()
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo(newName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.redirectUrl", Matchers.equalTo(redirectUrl)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hash", Matchers.equalTo(hash)))

            verify {
                shortLinkRepository.save(withArg {
                    assertEquals(it.name, newName)
                })
            }
        }


        @Test
        fun `should update ShortLink redirectUrl`() {
            val name = "someNewName"
            val newRedirectUrl = "https://example.com"
            val hash = "abcdefghi"

            every { shortLinkRepository.save(any()) } returns ShortLinkDao(
                1,
                name,
                newRedirectUrl,
                hash
            )

            every { shortLinkRepository.findById(1) } returns Optional.of(
                ShortLinkDao(
                    1,
                    name,
                    "https://old-example.com/",
                    hash
                )
            )

            mockMvc.perform(
                MockMvcRequestBuilders.put("/shortlinks/1").content(
                    """
            {
                "redirectUrl": "$newRedirectUrl"
            }
        """.trimIndent()
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo(name)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.redirectUrl", Matchers.equalTo(newRedirectUrl)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hash", Matchers.equalTo(hash)))

            verify {
                shortLinkRepository.save(withArg {
                    assertEquals(it.redirectUrl, newRedirectUrl)
                })
            }
        }

        @Test
        fun `should not update ShortLink properties if entity does not exist and return NotFound`() {
            val newRedirectUrl = "https://example.com"

            every { shortLinkRepository.findById(1) } returns Optional.empty()

            mockMvc.perform(
                MockMvcRequestBuilders.put("/shortlinks/1").content(
                    """
            {
                "redirectUrl": "$newRedirectUrl"
            }
        """.trimIndent()
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)

            verify(exactly = 0) { shortLinkRepository.save(any()) }
        }

        @ParameterizedTest(name = "should return BadRequest if name is: \"{0}\"")
        @EmptySource
        @ValueSource(strings = ["some super long name, indicating that it is invalid", "a"])
        fun `should return BadRequest on update if name is invalid`(newName: String?) {
            val redirectUrl = "https://example.com/"
            val hash = "abcdefghi"

            every { shortLinkRepository.save(any()) } returns ShortLinkDao(
                1,
                newName,
                redirectUrl,
                hash
            )

            every { shortLinkRepository.findById(1) } returns Optional.of(ShortLinkDao(1, "oldName", redirectUrl, hash))


            mockMvc.perform(
                MockMvcRequestBuilders.put("/shortlinks/1").content(
                    mapper.writeValueAsString(UpdateShortLinkDto(name = newName, redirectUrl = redirectUrl))
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

            verify(exactly = 0) { shortLinkRepository.save(any()) }
        }

        @ParameterizedTest(name = "should return BadRequest if redirectUrl is: \"{0}\"")
        @EmptySource
        @ValueSource(strings = ["https:not-valid-domain-with-protocol"])
        fun `should return BadRequest on create if redirectUrl is invalid`(newRedirectUrl: String?) {
            val name = "someName"
            val hash = "abcdefghi"

            every { shortLinkRepository.save(any()) } returns ShortLinkDao(
                1,
                name,
                newRedirectUrl,
                hash
            )

            every { shortLinkRepository.findById(1) } returns Optional.of(ShortLinkDao(1, name, "oldredirectUrl", hash))


            mockMvc.perform(
                MockMvcRequestBuilders.put("/shortlinks/1").content(
                    mapper.writeValueAsString(CreateShortLinkDto(name, newRedirectUrl))
                ).contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)

            verify(exactly = 0) { shortLinkRepository.save(any()) }
        }
    }

    @Nested
    @WebMvcTest
    @AutoConfigureMockMvc
    inner class DeleteShortLink(@Autowired val mockMvc: MockMvc) {

        @MockkBean
        private lateinit var shortLinkRepository: ShortLinkRepository

        @Test
        fun `should delete ShortLink by id`() {
            val toBeDeleted = ShortLinkDao(1, "name", "https://example.com", "abcdefghi")
            every { shortLinkRepository.findById(1) } returns Optional.of(toBeDeleted)

            every { shortLinkRepository.delete(any()) } returns Unit

            mockMvc.perform(
                MockMvcRequestBuilders.delete("/shortlinks/1").contentType(MediaType.APPLICATION_JSON)
            ).andExpect(MockMvcResultMatchers.status().isOk)

            verify {
                shortLinkRepository.delete(toBeDeleted)
            }
        }

        @Test
        fun `should return NotFound if ShortLink is not available`() {
            every { shortLinkRepository.findById(1) } returns Optional.empty()

            mockMvc.perform(
                MockMvcRequestBuilders.delete("/shortlinks/1").contentType(MediaType.APPLICATION_JSON)
            ).andExpect(MockMvcResultMatchers.status().isNotFound)

            verify(exactly = 0) { shortLinkRepository.delete(any()) }
        }
    }
}
