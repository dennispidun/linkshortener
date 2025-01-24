package me.dpidun.linkshortener.controller

import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.view.RedirectView
import java.net.URI

@RestController
@Tag(name = "Redirect", description = "Redirects to an URL of the provided shortLink hash provided within the path")
@RequestMapping("/")
class RedirectController(private val shortLinkRepository: ShortLinkRepository) {

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "404", description = "ShortLink not found", content = [],
            ),
            ApiResponse(
                responseCode = "302", content = [],
            )
        ]
    )
    @GetMapping("{hash}", produces = ["application/json"])
    fun redirect(@PathVariable hash: String): ResponseEntity<Any> {
        val shortLink = shortLinkRepository.findByHash(hash)

        if (shortLink?.redirectUrl == null) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "ShortLink not found"
            )
        }

        val redirectView = RedirectView()
        redirectView.setStatusCode(HttpStatus.FOUND)
        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI(shortLink.redirectUrl!!)
        return ResponseEntity(httpHeaders, HttpStatus.FOUND)
    }
}

