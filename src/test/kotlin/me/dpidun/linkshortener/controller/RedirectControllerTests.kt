package me.dpidun.linkshortener.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import me.dpidun.linkshortener.dao.ShortLinkDao
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class RedirectControllerTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @MockkBean
    private lateinit var shortLinkRepository: ShortLinkRepository

    @Test
    fun `should redirect to ShortLink`() {
        val hash = "abcdefghi"
        val shortLink = ShortLinkDao("some_name", "https://example.com", hash)

        every { shortLinkRepository.findByHash(hash) } returns shortLink

        mockMvc!!.perform(MockMvcRequestBuilders.get("/${hash}"))
            .andExpect(MockMvcResultMatchers.status().isFound())
            .andExpect(MockMvcResultMatchers.header().string("Location", "https://example.com"))
    }

    @Test
    fun `should respond with 404 for not existing hash`() {
        val hash = "abcdefghi"
        every { shortLinkRepository.findByHash(hash) } returns null

        mockMvc!!.perform(MockMvcRequestBuilders.get("/${hash}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }
}
