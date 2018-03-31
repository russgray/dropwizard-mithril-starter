package uk.me.shinyhead.core

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.apis.FacebookApi
import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import org.jvnet.hk2.annotations.Service
import org.slf4j.LoggerFactory
import uk.me.shinyhead.auth.OauthConfiguration
import uk.me.shinyhead.auth.OauthLoginRequest
import uk.me.shinyhead.core.mappers.OauthResponseMapper
import java.lang.invoke.MethodHandles
import java.net.URI
import javax.inject.Inject

@Service
class OauthService @Inject constructor(
        private val objectMapper: ObjectMapper,
        private val oauthConfiguration: OauthConfiguration) {

    //region Twitter

    fun getTwitterAuthzUrl(continueUri: URI): String {

        val service = ServiceBuilder()
                .apiKey(oauthConfiguration.twitterApiKey.value())
                .apiSecret(oauthConfiguration.twitterApiSecret.value())
                .callback(continueUri.toString())
                .build(TwitterApi.Authenticate.instance())

        return service.getAuthorizationUrl(service.requestToken)
    }

    fun makeTwitterLoginRequest(oauthToken: String, oauthVerifier: String): OauthLoginRequest {
        val rawResponse = getTwitterResponseBody(oauthToken, oauthVerifier)
        val responseDTO = objectMapper.readValue(rawResponse, TwitterOauthResponseDTO::class.java).apply {
            this.rawResponse = rawResponse
        }
        LOG.debug("Response from twitter: {}", responseDTO)
        return OauthResponseMapper.INSTANCE.toOauthRequest(responseDTO)
    }

    private fun getTwitterResponseBody(oauthToken: String, oauthVerifier: String): String {
        val service = ServiceBuilder()
                .apiKey(oauthConfiguration.twitterApiKey.value())
                .apiSecret(oauthConfiguration.twitterApiSecret.value())
                .build(TwitterApi.Authenticate.instance())

        val requestToken = OAuth1RequestToken(oauthToken, oauthVerifier)
        val accessToken = service.getAccessToken(requestToken, oauthVerifier)
        val request = OAuthRequest(Verb.GET, TWITTER_CREDENTIALS_ENDPOINT)

        service.signRequest(accessToken, request)
        return service.execute(request).body
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TwitterOauthResponseDTO(
            @field:JsonProperty
            var id: Long = 0,

            @field:JsonProperty("id_str")
            var idStr: String? = null,

            @field:JsonProperty("screen_name")
            var screenName: String? = null,

            @field:JsonProperty("time_zone")
            var timeZone: String? = null,

            @field:JsonProperty("utc_offset")
            var utcOffset: Long = 0,

            @field:JsonProperty("email")
            var email: String? = null,

            var rawResponse: String? = null)

    //endregion

    //region Facebook

    fun getFacebookAuthzUrl(continueUri: URI): String {
        val service = ServiceBuilder()
                .apiKey(oauthConfiguration.facebookApiKey.value())
                .apiSecret(oauthConfiguration.facebookApiSecret.value())
                .callback(continueUri.toString())
                .scope("email")
                .build(FacebookApi.instance())

        return service.authorizationUrl
    }

    fun makeFacebookLoginRequest(uri: URI, code: String): OauthLoginRequest {
        val rawResponse = getFacebookResponseBody(uri, code)
        val responseDTO = objectMapper.readValue(rawResponse, FacebookOauthResponseDTO::class.java).apply {
            this.rawResponse = rawResponse
        }
        LOG.debug("Response from facebook: {}", responseDTO)
        return OauthResponseMapper.INSTANCE.toOauthRequest(responseDTO)
    }

    private fun getFacebookResponseBody(uri: URI, code: String): String {
        val service = ServiceBuilder()
                .apiKey(oauthConfiguration.facebookApiKey.value())
                .apiSecret(oauthConfiguration.facebookApiSecret.value())
                .callback(uri.toString())
                .build(FacebookApi.instance())
        val accessToken = service.getAccessToken(code)
        val request = OAuthRequest(Verb.GET, FACEBOOK_CREDENTIALS_ENDPOINT)
        service.signRequest(accessToken, request)
        return service.execute(request).body
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class FacebookOauthResponseDTO(

            @field:JsonProperty
            var id: String? = null,

            @field:JsonProperty
            var email: String? = null,

            @field:JsonProperty
            var name: String? = null,

            @field:JsonProperty
            var locale: String? = null,

            @field:JsonProperty
            var timezone: Int = 0,

            var rawResponse: String? = null)

    //endregion

    companion object {
        const val TWITTER_CREDENTIALS_ENDPOINT = "https://api.twitter.com/1.1/account/verify_credentials.json?include_email=true"
        const val FACEBOOK_CREDENTIALS_ENDPOINT = "https://graph.facebook.com/v2.9/me?fields=email,name,link,locale,timezone,picture"

        val LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())!!
    }
}