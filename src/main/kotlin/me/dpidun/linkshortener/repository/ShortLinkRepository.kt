package me.dpidun.linkshortener.repository

import me.dpidun.linkshortener.dao.ShortLinkDao
import org.springframework.data.jpa.repository.JpaRepository

interface ShortLinkRepository : JpaRepository<ShortLinkDao, Long> {
    fun findByHash(hash: String): ShortLinkDao?
}
