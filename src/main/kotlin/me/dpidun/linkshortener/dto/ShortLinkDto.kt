package me.dpidun.linkshortener.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class CreateShortLinkDto(

    @field:Pattern(regexp = "[a-zA-Z0-9]{3,32}", message = "must be between 3 and 32 alphanumeric characters.")
    @field:NotNull
    @Schema(example = "ExampleShortLink")
    var name: String?,

    @field:Pattern(
        regexp = "^(https?://)?([\\w\\-])+\\.{1}([a-zA-Z]{2,63})([/\\w-]*)*/?\\??([^#\\n\\r]*)?#?([^\\n\\r]*)$",
        message = "invalid format, provide a common URL"
    )
    @field:NotNull
    @Schema(example = "https://example-with-a-long-domain-name.com/some/path/which?needs=to-be-shorter")
    var redirectUrl: String?
)


data class UpdateShortLinkDto(

    @field:Pattern(regexp = "[a-zA-Z0-9]{3,32}", message = "must be between 3 and 32 alphanumeric characters.")
    @Schema(example = "ExampleShortLink")
    var name: String?,

    @field:Pattern(
        regexp = "^(https?://)?([\\w\\-])+\\.{1}([a-zA-Z]{2,63})([/\\w-]*)*/?\\??([^#\\n\\r]*)?#?([^\\n\\r]*)$",
        message = "invalid format, provide a common URL"
    )
    @Schema(example = "https://example-with-a-long-domain-name.com/some/path/which?needs=to-be-shorter")
    var redirectUrl: String?
)

