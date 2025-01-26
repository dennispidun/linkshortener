package me.dpidun.linkshortener.repository

import me.dpidun.linkshortener.dao.ShortLinkDao
import org.springframework.data.repository.CrudRepository

interface ShortLinkRepository : CrudRepository<ShortLinkDao, Long> {
    fun findByHash(hash: String): ShortLinkDao?
}
