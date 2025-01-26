package me.dpidun.linkshortener.controller

import jakarta.validation.Valid
import me.dpidun.linkshortener.dao.ShortLinkDao
import me.dpidun.linkshortener.dto.CreateShortLinkDto
import me.dpidun.linkshortener.dto.GetShortLinksDto
import me.dpidun.linkshortener.dto.UpdateShortLinkDto
import me.dpidun.linkshortener.error.ShortLinkNotFoundException
import me.dpidun.linkshortener.repository.ShortLinkRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shortlinks")
class ShortLinkController(private val shortLinkRepository: ShortLinkRepository) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createShortLink(
        @Valid @RequestBody createCreateShortLinkDto: CreateShortLinkDto
    ): ShortLinkDao {
        return shortLinkRepository.save(
            ShortLinkDao(
                name = createCreateShortLinkDto.name,
                redirectUrl = createCreateShortLinkDto.redirectUrl
            )
        )
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getShortLinks(): GetShortLinksDto {
        return GetShortLinksDto(
            shortLinkRepository.findAll()
        )
    }

    @GetMapping("/{id}")
    fun getShortLink(
        @PathVariable id: Long
    ): ShortLinkDao {
        val shortLinkOptional = shortLinkRepository.findById(id)

        if (!shortLinkOptional.isPresent) {
            throw ShortLinkNotFoundException(id)
        }

        val shortLink = shortLinkOptional.get()

        return shortLink
    }


    @PutMapping("/{id}")
    fun updateShortLink(
        @PathVariable id: Long,
        @Valid @RequestBody updateCreateShortLinkDto: UpdateShortLinkDto
    ): ShortLinkDao {
        val shortLinkOptional = shortLinkRepository.findById(id)

        if (!shortLinkOptional.isPresent) {
            throw ShortLinkNotFoundException(id)
        }

        val shortLink = shortLinkOptional.get()

        shortLink.name = updateCreateShortLinkDto.name ?: shortLink.name
        shortLink.redirectUrl = updateCreateShortLinkDto.redirectUrl ?: shortLink.redirectUrl

        return shortLinkRepository.save(shortLink)
    }
}
