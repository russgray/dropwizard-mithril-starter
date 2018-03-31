package uk.me.shinyhead.auth

import uk.me.shinyhead.core.RemoteUserType

data class OauthLoginRequest(
        val idString: String,
        val remoteUserType: RemoteUserType,
        val timezone: String,
        val rawResponse: String,
        val email: String? = null,
        val nickname: String? = null)