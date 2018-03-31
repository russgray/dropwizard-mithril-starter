package uk.me.shinyhead.db.entities

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "users")
open class User(

        @GenericGenerator(
                name = "user_generator",
                strategy = "enhanced-sequence",
                parameters = [
                    (Parameter(name = "sequence_name", value = "users_user_id_seq")),
                    (Parameter(name = "optimizer", value = "pooled-lo")),
                    (Parameter(name = "increment_size", value = "5"))
                ])
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
        @Id
        @Column(name = "user_id")
        var id: Long? = null,

        @Column(name = "user_type", nullable = false)
        @Enumerated(EnumType.STRING)
        var userType: UserType? = null,

        @Column(name = "name", nullable = false)
        var name: String? = null,

        @Column(name = "email")
        var email: String? = null,

        @Column(name = "is_verified")
        var verified: Boolean = false,

        @Column(name = "remote_user_id", nullable = false)
        var remoteUserId: String? = null,

        @Column(name = "remote_user_type", nullable = false)
        @Enumerated(EnumType.STRING)
        var remoteUserType: RemoteUserType? = null,

        @OneToMany(mappedBy = "user",
                cascade = [CascadeType.ALL],
                orphanRemoval = true)
        var userRoles: MutableSet<UserRole> = mutableSetOf(),

        @Column(name = "created_at", insertable = false, updatable = false)
        var created: Instant? = null
) {

    fun addUserRole(userRole: UserRole) : User {
        userRoles.add(userRole)
        userRole.user = this
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "User(id=$id, userType=$userType, name=$name, email=$email, verified=$verified, remoteUserId=$remoteUserId, remoteUserType=$remoteUserType, created=$created)"
    }

    enum class UserType {
        OAUTH
    }

    enum class RemoteUserType {
        TWITTER,
        FACEBOOK
    }
}