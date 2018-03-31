package uk.me.shinyhead.resources

import com.codahale.metrics.annotation.Timed
import io.dropwizard.hibernate.UnitOfWork
import org.apache.shiro.authz.annotation.RequiresRoles
import org.apache.shiro.subject.Subject
import org.jvnet.hk2.annotations.Service
import org.secnod.shiro.jaxrs.Auth
import uk.me.shinyhead.core.SocialUser
import uk.me.shinyhead.core.UserService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Service
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@RequiresRoles(value = ["USER"])
class UserResource @Inject constructor(
        private val userService: UserService) {

    @GET
    @Timed
    @UnitOfWork
    @Path("me")
    fun me(@Auth subject: Subject) = userService.findById(subject.principals.oneByType(SocialUser::class.java).id)
}