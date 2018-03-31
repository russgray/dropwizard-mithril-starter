package uk.me.shinyhead.core

import org.jvnet.hk2.annotations.Service
import org.slf4j.LoggerFactory
import uk.me.shinyhead.auth.shiro.SocialLoginToken
import uk.me.shinyhead.core.mappers.UserMapper
import uk.me.shinyhead.db.RoleDao
import uk.me.shinyhead.db.UserDao
import uk.me.shinyhead.db.entities.User
import java.lang.invoke.MethodHandles
import javax.inject.Inject

@Service
class UserService @Inject constructor(private val userDao: UserDao, private val roleDao: RoleDao) {

    private val userMapper = UserMapper.INSTANCE

    fun findById(id: Long) = userMapper.mapSocialUser(userDao.findById(id))

    fun getOrCreate(socialLoginToken: SocialLoginToken): SocialUser {

        val remoteUserType = userMapper.map(socialLoginToken.remoteUserType)

        val user = userDao.findByRemoteId(socialLoginToken.remoteUserId, remoteUserType)
                .map { updateSocialLoginDetails(it, socialLoginToken) }
                .orElseGet({
                    val role = roleDao.getById("USER")
                    userDao.createUser(userMapper.map(socialLoginToken), role)
                })

        return userMapper.mapSocialUser(user)
    }

    private fun updateSocialLoginDetails(socialLoginUser: User, socialLoginToken: SocialLoginToken): User {

        if (socialLoginUser.email != socialLoginToken.email) {
            LOG.info("Detected change in email for user {} to {}", socialLoginUser, socialLoginToken.email)
            socialLoginUser.email = socialLoginToken.email
            socialLoginUser.verified = false
        }

        if (socialLoginUser.name != socialLoginToken.name) {
            LOG.info("Detected change in nickname for user {} to {}", socialLoginUser, socialLoginToken.name)
            socialLoginUser.name = socialLoginToken.name
        }

        return userDao.save(socialLoginUser)
    }

    companion object {
        val LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())!!
    }
}