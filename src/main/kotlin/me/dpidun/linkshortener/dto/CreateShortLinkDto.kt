package me.dpidun.linkshortener.dto

data class CreateShortLinkDto(
    var name: String,
    var redirectUrl: String
)
