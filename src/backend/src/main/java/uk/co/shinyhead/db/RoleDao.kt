package uk.me.shinyhead.db

import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import org.jvnet.hk2.annotations.Service
import uk.me.shinyhead.db.entities.Role
import javax.inject.Inject

@Service
class RoleDao @Inject constructor(sessionFactory: SessionFactory): AbstractDAO<Role>(sessionFactory) {

    fun getById(id: String): Role = get(id)
}