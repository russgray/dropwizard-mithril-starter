@file:Suppress("ClassName", "FunctionName")

package uk.me.shinyhead.core

import org.immutables.value.Value
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

abstract class Confidential<out T> {

    @Value.Parameter
    protected abstract fun _value(): T

    fun value(): T {
        if (LOG.isDebugEnabled) {
            LOG.debug("Unwrapping {} value from callsite {}", javaClass.simpleName, Throwable().stackTrace[1])
        }

        //noinspection deprecation
        return _value()
    }

    override fun toString(): String {
        return "${javaClass.simpleName}(<CONFIDENTIAL>)"
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}

@Value.Style(
        // Detect names starting with underscore
        typeAbstract = ["_*"],
        // Generate without any suffix, just raw detected name
        typeImmutable = "*",
        // Make generated it public, leave underscored as package private
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        // Seems unnecessary to have builder or superfluous copy method
        defaults = Value.Immutable(builder = false, copy = false))
annotation class Wrapped

@Value.Immutable
@Wrapped
abstract class _ExternalApiKey : Confidential<String>()

@Value.Immutable
@Wrapped
abstract class _ExternalApiSecret : Confidential<String>()
