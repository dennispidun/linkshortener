package me.dpidun.linkshortener.controller

import me.dpidun.linkshortener.dao.ShortLinkDao
import me.dpidun.linkshortener.dto.CreateShortLinkDto
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/shortlinks")
class ShortLinkController(private val shortLinkRepository: ShortLinkRepository) {

    @PostMapping
    fun createShortLink(@RequestBody createShortLinkDto: CreateShortLinkDto): String? {
        println(createShortLinkDto)

        return shortLinkRepository.save(
            ShortLinkDao(
                name = createShortLinkDto.name,
                redirectUrl = createShortLinkDto.redirectUrl
            )
        ).hash
    }
}
