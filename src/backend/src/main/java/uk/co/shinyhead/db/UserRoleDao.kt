package uk.me.shinyhead.db

import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import org.jvnet.hk2.annotations.Service
import uk.me.shinyhead.db.entities.UserRole
import javax.inject.Inject

@Service
class UserRoleDao @Inject constructor(sessionFactory: SessionFactory): AbstractDAO<UserRole>(sessionFactory) {

    fun getByUserId(userId: Long): List<UserRole> =
            list(query("FROM UserRole WHERE userId = :userId")
                    .setParameter("userId", userId))

}