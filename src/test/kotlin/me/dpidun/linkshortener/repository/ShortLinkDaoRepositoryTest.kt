package me.dpidun.linkshortener.repository

import me.dpidun.linkshortener.dao.ShortLinkDao
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class ShortLinkDaoRepositoryTest {

    @Autowired
    private lateinit var repository: ShortLinkRepository

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `should generate shortHash on save`() {
        val shortLink = ShortLinkDao("Test Link", "https://example.com")
        val savedShortLink = repository.save(shortLink)

        assertNotNull(savedShortLink.hash, "shortHash should not be null")
        assertEquals(8, savedShortLink.hash?.length, "shortHash should be 8 characters long")
    }

    @Test
    fun `should set createdAt on save`() {
        val shortLink = ShortLinkDao("Test Link", "https://example.com")
        val savedShortLink = repository.save(shortLink)

        assertNotNull(savedShortLink.createdAt, "createdAt should not be null")
    }

    @Test
    fun `should find by hash`() {
        val shortLink = ShortLinkDao("Test Link", "https://example.com")
        val savedShortLink = repository.save(shortLink)

        val foundShortLink = repository.findByHash(savedShortLink.hash!!)
        assertNotNull(foundShortLink, "ShortLink should be found by shortHash")
        assertEquals(savedShortLink.id, foundShortLink?.id, "Found ShortLink should have the same ID")
    }

    @Test
    fun `should have 8 digit hash`() {
        val shortLink = ShortLinkDao("Test Link", "https://example.com")
        val savedShortLink = repository.save(shortLink)

        assertEquals(8, savedShortLink.hash!!.length)
    }
}
