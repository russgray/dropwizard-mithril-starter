package uk.me.shinyhead.auth

import com.fasterxml.jackson.annotation.JsonProperty
import uk.me.shinyhead.core.ExternalApiKey
import uk.me.shinyhead.core.ExternalApiSecret
import javax.validation.constraints.NotNull

data class OauthConfiguration (

    @field:JsonProperty
    @field:NotNull
    var twitterApiKey: ExternalApiKey,

    @field:JsonProperty
    @field:NotNull
    var twitterApiSecret: ExternalApiSecret,

    @field:JsonProperty
    @field:NotNull
    var facebookApiKey: ExternalApiKey,

    @field:JsonProperty
    @field:NotNull
    var facebookApiSecret: ExternalApiSecret
)