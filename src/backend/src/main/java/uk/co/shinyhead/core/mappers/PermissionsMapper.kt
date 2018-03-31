package uk.me.shinyhead.core.mappers

import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import uk.me.shinyhead.api.PermissionDTO
import uk.me.shinyhead.core.Permission
import uk.me.shinyhead.db.entities.UserRole

@Mapper
abstract class PermissionsMapper {

    fun map(userRoles: Set<UserRole>): Set<Permission> {
        return userRoles
                .mapNotNull { it.role }
                .flatMap { role -> role.permissions.mapNotNull { perm -> Permission(role.name, perm.name) } }
                .toSet()
    }

    abstract fun map(permission: Permission): PermissionDTO

    companion object {
        val INSTANCE: PermissionsMapper = Mappers.getMapper(PermissionsMapper::class.java)
    }
}