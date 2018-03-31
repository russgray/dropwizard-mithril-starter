package uk.me.shinyhead.auth.shiro

import org.apache.commons.lang3.StringUtils
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.web.filter.AccessControlFilter
import org.apache.shiro.web.util.WebUtils
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.util.Arrays
import java.util.HashSet
import java.util.Locale
import java.util.Optional
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class CsrfFilter : AccessControlFilter() {

    var allowedHttpMethods: Set<String> = HashSet(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"))
    var authzScheme = HttpServletRequest.BASIC_AUTH
    var sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME
    var headerName = DEFAULT_CSRF_HEADER_NAME

    @Throws(Exception::class)
    override fun isAccessAllowed(
            servletRequest: ServletRequest,
            servletResponse: ServletResponse,
            mappedValue: Any?): Boolean {

        log.trace("Checking request for CSRF {}", servletRequest)

        val request = WebUtils.toHttp(servletRequest)

        // Bypass check for whitelisted methods
        if (allowedHttpMethods.contains(request.method.toUpperCase(Locale.ENGLISH))) {
            log.trace("Skipping CSRF check for request of method type {}", request.method)
            return true
        }

        // Bypass check for requests containing login credentials
        if (isLoginAttempt(request)) {
            log.trace("Skipping CSRF check for request containing auth {}", request)
            return true
        }

        // TODO: log more info on error
        val tokenFromSession = getTokenFromSession(request).orElseThrow<AuthorizationException>({ AuthorizationException() })
        val tokenFromRequestHeader = getTokenFromRequestHeader(request).orElseThrow<AuthorizationException>({ AuthorizationException() })

        if (StringUtils.equals(tokenFromSession, tokenFromRequestHeader)) {
            log.trace("CSRF check succeeded")
            return true
        }

        log.warn("CSRF token from {} does not match session value - possible hack attempt!", request)
        return false
    }

    private fun getTokenFromSession(request: HttpServletRequest): Optional<String> {
        var sessionToken: String? = null
        val session = request.getSession(false)
        if (session != null) {
            sessionToken = session.getAttribute(this.sessionAttributeName) as String
        }

        return Optional.ofNullable(sessionToken)
    }

    private fun getTokenFromRequestHeader(request: HttpServletRequest): Optional<String> {
        return Optional.ofNullable(request.getHeader(this.headerName))
    }

    @Throws(Exception::class)
    override fun onAccessDenied(servletRequest: ServletRequest, servletResponse: ServletResponse): Boolean {
        return false
    }

    protected fun isLoginAttempt(authzHeader: String): Boolean {
        val authzScheme = authzScheme.toLowerCase(Locale.ENGLISH)
        return authzHeader.toLowerCase(Locale.ENGLISH).startsWith(authzScheme)
    }

    /**
     * Determines whether the incoming request is an attempt to log in.
     *
     *
     * The default implementation obtains the value of the request's
     * [AUTHORIZATION_HEADER][.AUTHORIZATION_HEADER], and if it is not `null`, delegates
     * to [isLoginAttempt(authzHeaderValue)][.isLoginAttempt]. If the header is `null`,
     * `false` is returned.
     *
     * @param request  incoming ServletRequest
     * @return true if the incoming request is an attempt to log in based, false otherwise
     */
    protected fun isLoginAttempt(request: ServletRequest): Boolean {
        val authzHeader = getAuthzHeader(request)
        return authzHeader != null && isLoginAttempt(authzHeader)
    }

    /**
     * Returns the [AUTHORIZATION_HEADER][.AUTHORIZATION_HEADER] from the specified ServletRequest.
     *
     *
     * This implementation merely casts the request to an `HttpServletRequest` and returns the header:
     *
     *
     * `HttpServletRequest httpRequest = [toHttp(reaquest)][WebUtils.toHttp];<br></br>
     * return httpRequest.getHeader([AUTHORIZATION_HEADER][.AUTHORIZATION_HEADER]);`
     *
     * @param request the incoming `ServletRequest`
     * @return the `Authorization` header's value.
     */
    protected fun getAuthzHeader(request: ServletRequest): String? {
        val httpRequest = WebUtils.toHttp(request)
        return httpRequest.getHeader(AUTHORIZATION_HEADER)
    }

    companion object {

        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        private val DEFAULT_CSRF_HEADER_NAME = "X-CSRF-Token"
        private val DEFAULT_CSRF_TOKEN_ATTR_NAME = "SESSION_CSRF_TOKEN"
        private val AUTHORIZATION_HEADER = "Authorization"
    }
}
