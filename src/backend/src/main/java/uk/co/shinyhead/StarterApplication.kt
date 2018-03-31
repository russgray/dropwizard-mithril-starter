package uk.me.shinyhead

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.dropwizard.Application
import io.dropwizard.bundles.assets.ConfiguredAssetsBundle
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.hibernate.ScanningHibernateBundle
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import net.winterly.dropwizard.hk2bundle.HK2Bundle
import org.apache.shiro.realm.Realm
import org.eclipse.jetty.server.session.SessionHandler
import org.glassfish.hk2.api.Factory
import org.glassfish.hk2.api.ServiceLocator
import org.glassfish.hk2.utilities.ServiceLocatorUtilities
import org.glassfish.jersey.servlet.ServletProperties
import org.hibernate.SessionFactory
import org.secnod.dropwizard.shiro.ShiroBundle
import org.slf4j.LoggerFactory
import uk.me.shinyhead.auth.shiro.ShiroAuthcExceptionMapper
import uk.me.shinyhead.auth.shiro.ShiroAuthzExceptionMapper
import uk.me.shinyhead.auth.shiro.SocialAuthcRealm
import uk.me.shinyhead.core.UserService
import uk.me.shinyhead.db.RoleDao
import uk.me.shinyhead.db.UserDao
import uk.me.shinyhead.resources.OauthResource
import uk.me.shinyhead.resources.UserResource
import java.lang.invoke.MethodHandles

class StarterApplication : Application<StarterConfiguration>() {

    private val hibernateBundle = object : ScanningHibernateBundle<StarterConfiguration>("uk.me.shinyhead.db.entities") {
        override fun getDataSourceFactory(configuration: StarterConfiguration?) = configuration?.dataSourceFactory
    }

    private val migrationsBundle = object : MigrationsBundle<StarterConfiguration>() {
        override fun getDataSourceFactory(configuration: StarterConfiguration?) = configuration?.dataSourceFactory
    }

    private val shiroBundle = object : ShiroBundle<StarterConfiguration>() {
        override fun createRealms(configuration: StarterConfiguration?): MutableCollection<Realm> {
            val userService = UserService(
                    userDao = UserDao(hibernateBundle.sessionFactory),
                    roleDao = RoleDao(hibernateBundle.sessionFactory))

            val realm = UnitOfWorkAwareProxyFactory(hibernateBundle).create(
                    SocialAuthcRealm::class.java,
                    arrayOf(UserService::class.java),
                    arrayOf(userService))

            return mutableSetOf(realm)
        }

        override fun narrow(configuration: StarterConfiguration?) = configuration?.shiro
    }

    private val sessionFactory = object : Factory<SessionFactory> {
        override fun provide(): SessionFactory = hibernateBundle.sessionFactory
        override fun dispose(instance: SessionFactory?) {}
    }

    override fun getName(): String {
        return "Dropwizard Mithril Starter"
    }

    override fun initialize(bootstrap: Bootstrap<StarterConfiguration>) {

        bootstrap.objectMapper.registerKotlinModule()

        // Support environment variables in config file
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
                bootstrap.configurationSourceProvider,
                EnvironmentVariableSubstitutor(false))

        bootstrap.addBundle(HK2Bundle.builder().build())
        bootstrap.addBundle(hibernateBundle)
        bootstrap.addBundle(shiroBundle)
        bootstrap.addBundle(migrationsBundle)
        bootstrap.addBundle(ConfiguredAssetsBundle(mapOf("/assets" to "/"), "index.html"))
    }

    override fun run(configuration: StarterConfiguration,
                     environment: Environment) {

        val serviceLocator = environment
                .applicationContext
                .getAttribute(ServletProperties.SERVICE_LOCATOR) as ServiceLocator
        ServiceLocatorUtilities.addFactoryConstants(serviceLocator, sessionFactory)
        ServiceLocatorUtilities.addOneConstant<Any>(serviceLocator, configuration.oauth)
        ServiceLocatorUtilities.addOneConstant<Any>(serviceLocator, environment.objectMapper)

        environment.applicationContext.sessionHandler = SessionHandler()

        environment.jersey().register(ShiroAuthzExceptionMapper())
        environment.jersey().register(ShiroAuthcExceptionMapper())

        environment.jersey().register(OauthResource::class.java)
        environment.jersey().register(UserResource::class.java)
    }

    companion object {

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            StarterApplication().run(*args)
        }

        val LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())!!
    }
}

