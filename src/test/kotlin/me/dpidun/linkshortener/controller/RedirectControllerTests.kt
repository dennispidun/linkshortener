package me.dpidun.linkshortener.controller

import me.dpidun.linkshortener.dao.ShortLink
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class RedirectControllerTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @MockBean
    private lateinit var shortLinkRepository: ShortLinkRepository

    @Test
    fun shouldRedirectToShortLink() {
        val hash = "abcdefghi"
        val shortLink = ShortLink("some_name", "https://example.com", hash)

        Mockito.`when`(shortLinkRepository.findByHash(hash)).thenReturn(shortLink)

        mockMvc!!.perform(MockMvcRequestBuilders.get("/${hash}"))
            .andExpect(MockMvcResultMatchers.status().isFound())
            .andExpect(MockMvcResultMatchers.header().string("Location", "https://example.com"))
    }

    @Test
    fun shouldRespondWithNotFound() {
        val hash = "abcdefghi"
        Mockito.`when`(shortLinkRepository.findByHash(hash)).thenReturn(null)

        mockMvc!!.perform(MockMvcRequestBuilders.get("/${hash}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
    }
}
