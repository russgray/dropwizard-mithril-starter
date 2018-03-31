package uk.me.shinyhead.auth.shiro

import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthorizedException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class ShiroAuthzExceptionMapper : ExceptionMapper<AuthorizationException> {

    private data class ErrorResult(val msg: String, val status: Response.Status)

    override fun toResponse(exception: AuthorizationException?): Response {

        val (msg, status) = when (exception) {
            is UnauthorizedException -> ErrorResult("authorisation failure", Response.Status.FORBIDDEN)
            else -> ErrorResult("authentication failure", Response.Status.UNAUTHORIZED)
        }

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(mapOf("errors" to listOf(msg)))
                .build()
    }
}

class ShiroAuthcExceptionMapper : ExceptionMapper<AuthenticationException> {

    override fun toResponse(exception: AuthenticationException): Response =
            Response.status(Response.Status.UNAUTHORIZED)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(mapOf("errors" to listOf("authentication failure")))
                    .build()
}
