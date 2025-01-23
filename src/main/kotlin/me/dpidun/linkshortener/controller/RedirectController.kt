package me.dpidun.linkshortener.controller

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.RedirectView


@Controller
@RequestMapping("/")
class RedirectController {

    @GetMapping("{candidate}")
    fun redirect(@PathVariable candidate: String): RedirectView {
        val redirectView = RedirectView("https://www.${candidate}.com")
        redirectView.setStatusCode(HttpStatus.FOUND)
        return redirectView
    }
}

