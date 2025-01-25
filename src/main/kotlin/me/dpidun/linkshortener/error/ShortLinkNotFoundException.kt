package me.dpidun.linkshortener.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such ShortLink")
class ShortLinkNotFoundException(id: Long) : RuntimeException("Could not find ShortLink(id: $id).")
