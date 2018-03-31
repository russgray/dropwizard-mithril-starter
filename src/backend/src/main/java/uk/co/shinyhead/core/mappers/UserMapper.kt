package uk.me.shinyhead.core.mappers

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import uk.me.shinyhead.api.UserDTO
import uk.me.shinyhead.auth.shiro.SocialLoginToken
import uk.me.shinyhead.core.RemoteUserType
import uk.me.shinyhead.core.SocialUser
import uk.me.shinyhead.core.UserType
import uk.me.shinyhead.db.entities.User

@Mapper(uses = [PermissionsMapper::class])
abstract class UserMapper {

    private val permissionsMapper = PermissionsMapper.INSTANCE

    fun mapSocialUser(user: User): SocialUser {

        return SocialUser(
                id = user.id!!,
                userType = map(user.userType!!),
                name = user.name,
                email = user.email,
                verified = user.verified,
                remoteUserId = user.remoteUserId!!,
                remoteUserType = map(user.remoteUserType!!),
                permissions = permissionsMapper.map(user.userRoles),
                created = user.created)
    }

    @Mappings(value = [
        Mapping(target = "id", ignore = true),
        Mapping(target = "userType", constant = "OAUTH"),
        Mapping(target = "verified", ignore = true),
        Mapping(target = "userRoles", ignore = true),
        Mapping(target = "created", ignore = true)
    ])
    abstract fun map(socialLoginToken: SocialLoginToken): User

    abstract fun map(user: SocialUser): UserDTO

    abstract fun map(userType: UserType): User.UserType

    abstract fun map(userType: User.UserType): UserType

    abstract fun map(remoteUserType: RemoteUserType): User.RemoteUserType

    abstract fun map(remoteUserType: User.RemoteUserType): RemoteUserType

    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }
}