package uk.me.shinyhead.auth.shiro

import org.apache.shiro.authc.HostAuthenticationToken
import uk.me.shinyhead.core.RemoteUserType

class SocialLoginToken(
        var name: String? = null,
        var nickname: String? = null,
        var email: String? = null,
        val remoteUserId: String,
        val remoteUserType: RemoteUserType,
        private val accessToken: String,
        private var host: String? = null) : HostAuthenticationToken {

    override fun getHost() = host
    override fun getCredentials() = accessToken
    override fun getPrincipal() = SocialLoginPrincipal(remoteUserId, remoteUserType)
}

data class SocialLoginPrincipal(val remoteUserId: String, val remoteUserType: RemoteUserType)
