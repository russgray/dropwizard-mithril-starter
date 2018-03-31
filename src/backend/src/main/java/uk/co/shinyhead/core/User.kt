package uk.me.shinyhead.core

import java.time.Instant

data class SocialUser(
        val id: Long,
        val userType: UserType,
        val name: String?,
        val email: String?,
        val verified: Boolean,
        val remoteUserId: String,
        val remoteUserType: RemoteUserType,
        val permissions: Set<Permission>,
        val created: Instant?
) {
    override fun toString(): String {
        return "SocialUser(id=$id, userType=$userType, verified=$verified, remoteUserId='$remoteUserId', remoteUserType=$remoteUserType)"
    }
}

data class Permission(val roleName: String, val permissionName: String)
