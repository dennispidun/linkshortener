package me.dpidun.linkshortener.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern


data class CreateShortLinkDto(

    @field:Pattern(regexp = "[a-zA-Z0-9]{3,32}", message = "must be between 3 and 32 alphanumeric characters.")
    @field:NotNull
    @field:NotBlank
    var name: String?,

    @field:Pattern(
        regexp = "^(https?://)?([\\w\\-])+\\.{1}([a-zA-Z]{2,63})([/\\w-]*)*/?\\??([^#\\n\\r]*)?#?([^\\n\\r]*)$",
        message = "invalid format, provide a common URL"
    )
    @field:NotNull
    @field:NotBlank
    var redirectUrl: String?
)
