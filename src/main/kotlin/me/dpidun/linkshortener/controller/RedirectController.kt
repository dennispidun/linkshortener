package me.dpidun.linkshortener.controller

import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.view.RedirectView


@Controller
@RequestMapping("/")
class RedirectController(private val shortLinkRepository: ShortLinkRepository) {

    @GetMapping("{hash}")
    fun redirect(@PathVariable hash: String): RedirectView {
        val shortLink = shortLinkRepository.findByHash(hash)

        if (shortLink?.redirectUrl == null) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "ShortLink not found"
            )
        }

        val redirectView = RedirectView(shortLink.redirectUrl!!)
        redirectView.setStatusCode(HttpStatus.FOUND)
        return redirectView
    }
}

