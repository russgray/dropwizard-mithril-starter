package uk.me.shinyhead.db

import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import org.jvnet.hk2.annotations.Service
import uk.me.shinyhead.db.entities.Role
import uk.me.shinyhead.db.entities.User
import uk.me.shinyhead.db.entities.UserRole
import java.util.Optional
import javax.inject.Inject

@Service
class UserDao @Inject constructor(sessionFactory: SessionFactory) : AbstractDAO<User>(sessionFactory) {

    fun save(user: User): User = persist(user)

    fun createUser(user: User, role: Role): User {
        user.addUserRole(UserRole(role = role))
        persist(user)
        return findById(user.id!!)
    }

    fun findById(id: Long): User =
            query("""
                FROM User user
                JOIN FETCH user.userRoles roles
                JOIN FETCH roles.role role
                JOIN FETCH role.permissions permissions
                WHERE user.id = :id
                """).setParameter("id", id)
                    .singleResult

    fun findByRemoteId(remoteUserId: String, remoteUserType: User.RemoteUserType): Optional<User> =
            query("""
                FROM User user
                JOIN FETCH user.userRoles roles
                JOIN FETCH roles.role role
                JOIN FETCH role.permissions permissions
                WHERE user.remoteUserId = :remoteUserId
                  AND user.remoteUserType = :remoteUserType
                """).setParameter("remoteUserId", remoteUserId)
                    .setParameter("remoteUserType", remoteUserType)
                    .uniqueResultOptional()
}