package me.dpidun.linkshortener.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import me.dpidun.linkshortener.dao.ShortLinkDao
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.junit.jupiter.api.Test
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

    @Test
    fun `should create ShortLink`() {
        val name = "some_name"
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


    @Test
    fun `should return bad request for missing redirect url`() {
        val name = "some_name"

        mockMvc.perform(
            MockMvcRequestBuilders.post("/shortlinks").content(
                """
            {
                "name": "$name"
                "someOtherParams": "hello world"
            }
        """.trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

        verify(exactly = 0) { shortLinkRepository.save(any()) }
    }

    @Test
    fun `should bad request for malformed body`() {
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
