package me.dpidun.linkshortener.dao

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.security.MessageDigest
import java.time.LocalDateTime


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class ShortLinkDao {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    var hash: String? = null
    var name: String? = null
    var redirectUrl: String? = null

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    protected constructor()

    constructor(name: String?, redirectUrl: String?) {
        this.name = name
        this.redirectUrl = redirectUrl
        this.createdAt = LocalDateTime.now()
    }

    constructor(name: String?, redirectUrl: String?, hash: String?) : this(name, redirectUrl) {
        this.hash = hash
    }

    constructor(id: Long?, name: String?, redirectUrl: String?, hash: String?) : this(name, redirectUrl, hash) {
        this.id = id
    }

    @PrePersist
    @PreUpdate
    private fun generateShortHash() {
        if (hash.isNullOrEmpty() && !name.isNullOrEmpty() && !redirectUrl.isNullOrEmpty() && createdAt != null) {
            hash = hash(name + redirectUrl + createdAt)
        }
    }

    // see https://gist.github.com/lovubuntu/164b6b9021f5ba54cefc67f60f7a1a25
    private fun hash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }.take(8)
    }

    override fun toString(): String {
        return "ShortLinkDao(id=$id, hash=$hash, name=$name, redirectUrl=$redirectUrl, createdAt=$createdAt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShortLinkDao

        if (id != other.id) return false
        if (hash != other.hash) return false
        if (name != other.name) return false
        if (redirectUrl != other.redirectUrl) return false
        if (createdAt != other.createdAt) return false

        return true
    }
}
