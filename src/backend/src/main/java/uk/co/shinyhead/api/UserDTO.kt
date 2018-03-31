package uk.me.shinyhead.api

import com.fasterxml.jackson.annotation.JsonProperty

data class UserDTO(
        @JsonProperty
        var userType: String? = null,

        @JsonProperty
        var name: String? = null,

        @JsonProperty
        var email: String? = null,

        @JsonProperty
        var verified: Boolean? = null,

        @JsonProperty
        var remoteUserId: String? = null,

        @JsonProperty
        var remoteUserType: String? = null,

        @JsonProperty
        var permissions: List<PermissionDTO> = listOf())

data class PermissionDTO(
        @JsonProperty
        var roleName: String? = null,

        @JsonProperty
        var permissionName: String? = null)