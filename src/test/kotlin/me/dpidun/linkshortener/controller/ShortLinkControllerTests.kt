package me.dpidun.linkshortener.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import io.swagger.v3.core.util.Json
import me.dpidun.linkshortener.dao.ShortLinkDao
import me.dpidun.linkshortener.dto.CreateShortLinkDto
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import kotlin.test.assertEquals

@WebMvcTest
@AutoConfigureMockMvc
class ShortLinkControllerTests(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var shortLinkRepository: ShortLinkRepository

    private val mapper = Json.mapper()

    @Test
    fun `should create ShortLink`() {
        val name = "someName"
        val redirectUrl = "https://example.com"

        every { shortLinkRepository.save(any()) } returns ShortLinkDao(
            name,
            redirectUrl,
            "abcdefghi"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/shortlinks").content(
                """
            {
                "name": "$name",
                "redirectUrl": "$redirectUrl"
            }
        """.trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        verify {
            shortLinkRepository.save(withArg {
                assertEquals(it.name, name)
                assertEquals(it.redirectUrl, redirectUrl)
            })
        }
    }

    @ParameterizedTest(name = "should return BadRequest if name is: {0}")
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

    @ParameterizedTest(name = "should return BadRequest if redirectUrl is: {0}")
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
