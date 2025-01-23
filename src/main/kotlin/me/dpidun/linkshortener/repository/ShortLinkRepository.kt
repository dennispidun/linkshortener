package me.dpidun.linkshortener.repository

import me.dpidun.linkshortener.dao.ShortLink
import org.springframework.data.jpa.repository.JpaRepository

interface ShortLinkRepository : JpaRepository<ShortLink, Long> {
    fun findByHash(hash: String): ShortLink?
}
