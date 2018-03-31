package uk.me.shinyhead

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.bundles.assets.AssetsBundleConfiguration
import io.dropwizard.bundles.assets.AssetsConfiguration
import io.dropwizard.db.DataSourceFactory
import org.secnod.dropwizard.shiro.ShiroConfiguration
import uk.me.shinyhead.auth.OauthConfiguration
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class StarterConfiguration(

        @field:NotNull
        @field:Valid
        @field:JsonProperty("database")
        val dataSourceFactory: DataSourceFactory,

        @field:NotNull
        @field:JsonProperty("dataFiles")
        val dataFiles: List<String>,

        @field:NotNull
        @field:JsonProperty("shiro")
        val shiro: ShiroConfiguration,

        @field:NotNull
        @field:JsonProperty("oauth")
        val oauth: OauthConfiguration,

        @field:NotNull
        @field:JsonProperty("assets")
        val assets: AssetsConfiguration

) : Configuration(), AssetsBundleConfiguration {
    override fun getAssetsConfiguration() = assets
}
