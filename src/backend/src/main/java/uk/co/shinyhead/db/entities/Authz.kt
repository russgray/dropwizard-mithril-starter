package uk.me.shinyhead.db.entities

import org.apache.commons.lang3.StringUtils
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Immutable
import org.hibernate.annotations.Parameter
import java.io.Serializable
import java.time.LocalDateTime
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "roles")
@Immutable
data class Role(

        @Id
        @Column(name = "role_name", nullable = false)
        var name: String,

        @Column
        var description: String? = null,

        @OneToMany(mappedBy = "role")
        var permissions: Set<Permission> = setOf(),

        @OneToMany(mappedBy = "role")
        var userRoles: Set<UserRole> = setOf(),

        @Column(name = "created_at", insertable = false, updatable = false)
        var created: LocalDateTime? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Role
        return StringUtils.equals(name, other.name)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(name)
    }

    override fun toString(): String {
        return "Role(name='$name')"
    }

}


@Entity
@Table(name = "permissions")
@Immutable
data class Permission(

        @Id
        @Column(name = "permission_id")
        var id: Int? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "role_name")
        var role: Role? = null,

        @Column(name = "permission_name")
        var name: String,

        @Column(name = "created_at", insertable = false, updatable = false)
        var created: LocalDateTime? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Permission

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Permission(name='$name')"
    }
}


@Entity
@Table(name = "user_roles")
@Immutable
data class UserRole(

        @GenericGenerator(
                name = "user_role_generator",
                strategy = "enhanced-sequence",
                parameters = [
                    (Parameter(name = "sequence_name", value = "user_roles_user_role_id_seq")),
                    (Parameter(name = "optimizer", value = "pooled-lo")),
                    (Parameter(name = "increment_size", value = "5"))
                ])
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_generator")
        @Id
        @Column(name = "user_role_id")
        var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "role_name")
        var role: Role? = null,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User? = null,

        @Column(name = "created_at", insertable = false, updatable = false)
        var created: LocalDateTime? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as UserRole

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "UserRole(id=$id)"
    }
}

