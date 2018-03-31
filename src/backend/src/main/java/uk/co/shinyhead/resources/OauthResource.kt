package uk.me.shinyhead.resources

import com.codahale.metrics.annotation.Timed
import io.dropwizard.hibernate.UnitOfWork
import org.apache.shiro.subject.Subject
import org.jvnet.hk2.annotations.Service
import org.secnod.shiro.jaxrs.Auth
import org.slf4j.LoggerFactory
import uk.me.shinyhead.api.UserDTO
import uk.me.shinyhead.auth.shiro.SocialLoginToken
import uk.me.shinyhead.core.OauthService
import uk.me.shinyhead.core.RemoteUserType
import uk.me.shinyhead.core.SocialUser
import uk.me.shinyhead.core.UserService
import uk.me.shinyhead.core.mappers.UserMapper
import java.lang.invoke.MethodHandles
import java.net.URI
import java.util.UUID
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Service
@Path("/oauth")
@Produces(MediaType.APPLICATION_JSON)
class OauthResource @Inject constructor(
        private val userService: UserService,
        private val oauthService: OauthService) {

    private val userMapper = UserMapper.INSTANCE

    @POST
    @Timed
    @UnitOfWork
    fun createTestUser(@Auth subject: Subject, @Valid userDTO: UserDTO): UserDTO {
        val socialLoginToken = SocialLoginToken(
                name = userDTO.name,
                email = userDTO.email,
                remoteUserId = userDTO.remoteUserId!!,
                remoteUserType = RemoteUserType.valueOf(userDTO.remoteUserType!!),
                accessToken = "")

        subject.login(socialLoginToken)
        return userMapper.map(userService.getOrCreate(socialLoginToken))
    }

    @GET
    @Timed
    @Path("/twitter")
    fun redirectToTwitter(@Context uriInfo: UriInfo): Response {
        val continueUri = uriInfo.absolutePathBuilder.path("continue").build()
        return Response.seeOther(URI.create(oauthService.getTwitterAuthzUrl(continueUri))).build()
    }

    @GET
    @Timed
    @Path("/twitter/continue")
    fun redirectToAppFromTwitter(
            @Auth subject: Subject,
            @QueryParam("oauth_token") oauthToken: String,
            @QueryParam("oauth_verifier") oauthVerifier: String): Response {

        val loginRequest = oauthService.makeTwitterLoginRequest(oauthToken, oauthVerifier)
        subject.login(SocialLoginToken(
                remoteUserId = loginRequest.idString,
                remoteUserType = loginRequest.remoteUserType,
                accessToken = "",
                nickname = loginRequest.nickname,
                email = loginRequest.email,
                host = null))

        return makeLoginSuccessResponse(Response.seeOther(AUTHENTICATED_REDIRECT_URI), subject).build()
    }

    @GET
    @Timed
    @Path("/facebook")
    fun redirectToFacebook(@Context uriInfo: UriInfo): Response {
        val continueUri = uriInfo.absolutePathBuilder.path("continue").build()
        return Response.seeOther(URI.create(oauthService.getFacebookAuthzUrl(continueUri))).build()
    }

    @GET
    @Timed
    @Path("/facebook/continue")
    fun redirectToAppFromFacebook(
            @Auth subject: Subject,
            @Context uriInfo: UriInfo,
            @QueryParam("code") code: String): Response {

        val loginRequest = oauthService.makeFacebookLoginRequest(uriInfo.absolutePath, code)
        subject.login(SocialLoginToken(
                remoteUserId = loginRequest.idString,
                remoteUserType = loginRequest.remoteUserType,
                accessToken = "",
                nickname = loginRequest.nickname,
                email = loginRequest.email,
                host = null))

        return makeLoginSuccessResponse(Response.seeOther(AUTHENTICATED_REDIRECT_URI), subject).build()
    }

    private fun makeLoginSuccessResponse(response: Response.ResponseBuilder, subject: Subject): Response.ResponseBuilder {
        val csrfToken = UUID.randomUUID().toString()
        subject.session.setAttribute(CSRF_TOKEN_ATTR_NAME, csrfToken)

        val user = subject.principals.oneByType(SocialUser::class.java)

        return response
                .header(CSRF_TOKEN_HEADER_NAME, csrfToken)
                .entity(userMapper.map(user))
    }

    companion object {
        const val CSRF_TOKEN_HEADER_NAME = "X-CSRF-Token"
        const val CSRF_TOKEN_ATTR_NAME = "SESSION_CSRF_TOKEN"

        val AUTHENTICATED_REDIRECT_URI = URI("/#!/authenticated")
        val LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())!!
    }
}