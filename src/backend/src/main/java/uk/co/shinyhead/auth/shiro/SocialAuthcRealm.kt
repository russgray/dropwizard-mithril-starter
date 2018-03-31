package uk.me.shinyhead.auth.shiro

import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection
import org.jvnet.hk2.annotations.Service
import org.slf4j.LoggerFactory
import uk.me.shinyhead.core.SocialUser
import uk.me.shinyhead.core.UserService
import java.lang.invoke.MethodHandles
import javax.inject.Inject

@Service
open class SocialAuthcRealm @Inject constructor(

        private val userService: UserService

) : AuthorizingRealm(AllowAllCredentialsMatcher()) {

    override fun supports(token: AuthenticationToken?) = token != null && token is SocialLoginToken

    override fun doGetAuthenticationInfo(token: AuthenticationToken?): AuthenticationInfo {
        val socialLoginToken = token as SocialLoginToken
        val socialUser = userService.getOrCreate(socialLoginToken)

        val principals = SimplePrincipalCollection(setOf(socialUser), REALM)
        val credentials = socialLoginToken.credentials

        return SimpleAuthenticationInfo(principals, credentials)
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo? {
        val user = principals.oneByType(SocialUser::class.java) ?: return null

        val roleNames = user.permissions.map { it.roleName }.toSet()
        val permissionNames = user.permissions.map { it.permissionName }.toSet()

        if (LOG.isDebugEnabled) {
            LOG.debug("Authorizing user {} with roles {} and permissions {}", user, roleNames, permissionNames)
        } else {
            LOG.debug("Authorizing user {}", user)
        }

        return SimpleAuthorizationInfo().apply {
            roles = roleNames
            stringPermissions = permissionNames
        }
    }

    companion object {
        const val REALM = "social"
        val LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())!!
    }
}