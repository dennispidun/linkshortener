package me.dpidun.linkshortener.controller

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(RedirectController::class)
class RedirectControllerTest {
    @Autowired
    private val mockMvc: MockMvc? = null

    @ParameterizedTest
    @ValueSource(strings = ["google", "github", "bing"])
    fun shouldRedirectToDotComDomain(candidate: String) {
        mockMvc!!.perform(MockMvcRequestBuilders.get("/${candidate}"))
            .andExpect(MockMvcResultMatchers.status().isFound()) // HTTP 302
            .andExpect(MockMvcResultMatchers.header().string("Location", "https://www.${candidate}.com"))
    }
}
