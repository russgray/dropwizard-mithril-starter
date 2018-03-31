package uk.me.shinyhead.db

import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import org.jvnet.hk2.annotations.Service
import uk.me.shinyhead.db.entities.Permission
import javax.inject.Inject

@Service
class PermissionDao @Inject constructor(sessionFactory: SessionFactory) : AbstractDAO<Permission>(sessionFactory) {

    fun getPermissionsForRole(roleName: String): List<Permission> =
            list(query("FROM Permission WHERE roleName = :roleName")
                    .setParameter("roleName", roleName))
}