package uk.me.shinyhead.core.mappers

import io.vavr.kotlin.`try`
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTimeZone
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.slf4j.LoggerFactory
import uk.me.shinyhead.auth.OauthLoginRequest
import uk.me.shinyhead.core.OauthService
import uk.me.shinyhead.core.RemoteUserType
import java.lang.invoke.MethodHandles

@Mapper
abstract class OauthResponseMapper {

    fun toOauthRequest(dto: OauthService.FacebookOauthResponseDTO): OauthLoginRequest {
        return OauthLoginRequest(
                idString =  dto.id!!,
                remoteUserType = RemoteUserType.FACEBOOK,
                timezone = timezoneFromResponse(dto),
                nickname = dto.name,
                email = StringUtils.lowerCase(dto.email),
                rawResponse = dto.rawResponse!!)
    }

    fun toOauthRequest(dto: OauthService.TwitterOauthResponseDTO): OauthLoginRequest {
        return OauthLoginRequest(
                idString = dto.idStr!!,
                remoteUserType = RemoteUserType.TWITTER,
                timezone = timezoneFromResponse(dto),
                nickname = dto.screenName,
                email = StringUtils.lowerCase(dto.email),
                rawResponse = dto.rawResponse!!)
    }

    private fun timezoneFromResponse(dto: OauthService.TwitterOauthResponseDTO): String {

        // Default to UTC if no timezone is specified, or timezone cannot be found
        val key = dto.timeZone ?: "UTC"

        return TWITTER_TZ_MAP.getOrElse(key, {
            log.warn("Unable to map twitter timezone {}, using UTC", dto.timeZone)
            return DateTimeZone.UTC.id
        })
    }

    private fun timezoneFromResponse(dto: OauthService.FacebookOauthResponseDTO): String {
        return `try` { DateTimeZone.forOffsetHours(dto.timezone) }
                .getOrElse { DateTimeZone.UTC }
                .id
    }

    companion object {

        val INSTANCE: OauthResponseMapper = Mappers.getMapper(OauthResponseMapper::class.java)

        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        private val TWITTER_TZ_MAP = mapOf(
                "International Date Line West" to "Pacific/Midway",
                "Midway Island" to "Pacific/Midway",
                "American Samoa" to "Pacific/Pago_Pago",
                "Hawaii" to "Pacific/Honolulu",
                "Alaska" to "America/Juneau",
                "Pacific Time (US & Canada)" to "America/Los_Angeles",
                "Tijuana" to "America/Tijuana",
                "Mountain Time (US & Canada)" to "America/Denver",
                "Arizona" to "America/Phoenix",
                "Chihuahua" to "America/Chihuahua",
                "Mazatlan" to "America/Mazatlan",
                "Central Time (US & Canada)" to "America/Chicago",
                "Saskatchewan" to "America/Regina",
                "Guadalajara" to "America/Mexico_City",
                "Mexico City" to "America/Mexico_City",
                "Monterrey" to "America/Monterrey",
                "Central America" to "America/Guatemala",
                "Eastern Time (US & Canada)" to "America/New_York",
                "Indiana (East)" to "America/Indiana/Indianapolis",
                "Bogota" to "America/Bogota",
                "Lima" to "America/Lima",
                "Quito" to "America/Lima",
                "Atlantic Time (Canada)" to "America/Halifax",
                "Caracas" to "America/Caracas",
                "La Paz" to "America/La_Paz",
                "Santiago" to "America/Santiago",
                "Newfoundland" to "America/St_Johns",
                "Brasilia" to "America/Sao_Paulo",
                "Buenos Aires" to "America/Argentina/Buenos_Aires",
                "Montevideo" to "America/Montevideo",
                "Georgetown" to "America/Guyana",
                "Greenland" to "America/Godthab",
                "Mid-Atlantic" to "Atlantic/South_Georgia",
                "Azores" to "Atlantic/Azores",
                "Cape Verde Is." to "Atlantic/Cape_Verde",
                "Dublin" to "Europe/Dublin",
                "Edinburgh" to "Europe/London",
                "Lisbon" to "Europe/Lisbon",
                "London" to "Europe/London",
                "Casablanca" to "Africa/Casablanca",
                "Monrovia" to "Africa/Monrovia",
                "UTC" to "Etc/UTC",
                "Belgrade" to "Europe/Belgrade",
                "Bratislava" to "Europe/Bratislava",
                "Budapest" to "Europe/Budapest",
                "Ljubljana" to "Europe/Ljubljana",
                "Prague" to "Europe/Prague",
                "Sarajevo" to "Europe/Sarajevo",
                "Skopje" to "Europe/Skopje",
                "Warsaw" to "Europe/Warsaw",
                "Zagreb" to "Europe/Zagreb",
                "Brussels" to "Europe/Brussels",
                "Copenhagen" to "Europe/Copenhagen",
                "Madrid" to "Europe/Madrid",
                "Paris" to "Europe/Paris",
                "Amsterdam" to "Europe/Amsterdam",
                "Berlin" to "Europe/Berlin",
                "Bern" to "Europe/Zurich",
                "Zurich" to "Europe/Zurich",
                "Rome" to "Europe/Rome",
                "Stockholm" to "Europe/Stockholm",
                "Vienna" to "Europe/Vienna",
                "West Central Africa" to "Africa/Algiers",
                "Bucharest" to "Europe/Bucharest",
                "Cairo" to "Africa/Cairo",
                "Helsinki" to "Europe/Helsinki",
                "Kyiv" to "Europe/Kiev",
                "Riga" to "Europe/Riga",
                "Sofia" to "Europe/Sofia",
                "Tallinn" to "Europe/Tallinn",
                "Vilnius" to "Europe/Vilnius",
                "Athens" to "Europe/Athens",
                "Istanbul" to "Europe/Istanbul",
                "Minsk" to "Europe/Minsk",
                "Jerusalem" to "Asia/Jerusalem",
                "Harare" to "Africa/Harare",
                "Pretoria" to "Africa/Johannesburg",
                "Kaliningrad" to "Europe/Kaliningrad",
                "Moscow" to "Europe/Moscow",
                "St. Petersburg" to "Europe/Moscow",
                "Volgograd" to "Europe/Volgograd",
                "Samara" to "Europe/Samara",
                "Kuwait" to "Asia/Kuwait",
                "Riyadh" to "Asia/Riyadh",
                "Nairobi" to "Africa/Nairobi",
                "Baghdad" to "Asia/Baghdad",
                "Tehran" to "Asia/Tehran",
                "Abu Dhabi" to "Asia/Muscat",
                "Muscat" to "Asia/Muscat",
                "Baku" to "Asia/Baku",
                "Tbilisi" to "Asia/Tbilisi",
                "Yerevan" to "Asia/Yerevan",
                "Kabul" to "Asia/Kabul",
                "Ekaterinburg" to "Asia/Yekaterinburg",
                "Islamabad" to "Asia/Karachi",
                "Karachi" to "Asia/Karachi",
                "Tashkent" to "Asia/Tashkent",
                "Chennai" to "Asia/Kolkata",
                "Kolkata" to "Asia/Kolkata",
                "Mumbai" to "Asia/Kolkata",
                "New Delhi" to "Asia/Kolkata",
                "Kathmandu" to "Asia/Kathmandu",
                "Astana" to "Asia/Dhaka",
                "Dhaka" to "Asia/Dhaka",
                "Sri Jayawardenepura" to "Asia/Colombo",
                "Almaty" to "Asia/Almaty",
                "Novosibirsk" to "Asia/Novosibirsk",
                "Rangoon" to "Asia/Rangoon",
                "Bangkok" to "Asia/Bangkok",
                "Hanoi" to "Asia/Bangkok",
                "Jakarta" to "Asia/Jakarta",
                "Krasnoyarsk" to "Asia/Krasnoyarsk",
                "Beijing" to "Asia/Shanghai",
                "Chongqing" to "Asia/Chongqing",
                "Hong Kong" to "Asia/Hong_Kong",
                "Urumqi" to "Asia/Urumqi",
                "Kuala Lumpur" to "Asia/Kuala_Lumpur",
                "Singapore" to "Asia/Singapore",
                "Taipei" to "Asia/Taipei",
                "Perth" to "Australia/Perth",
                "Irkutsk" to "Asia/Irkutsk",
                "Ulaanbaatar" to "Asia/Ulaanbaatar",
                "Seoul" to "Asia/Seoul",
                "Osaka" to "Asia/Tokyo",
                "Sapporo" to "Asia/Tokyo",
                "Tokyo" to "Asia/Tokyo",
                "Yakutsk" to "Asia/Yakutsk",
                "Darwin" to "Australia/Darwin",
                "Adelaide" to "Australia/Adelaide",
                "Canberra" to "Australia/Melbourne",
                "Melbourne" to "Australia/Melbourne",
                "Sydney" to "Australia/Sydney",
                "Brisbane" to "Australia/Brisbane",
                "Hobart" to "Australia/Hobart",
                "Vladivostok" to "Asia/Vladivostok",
                "Guam" to "Pacific/Guam",
                "Port Moresby" to "Pacific/Port_Moresby",
                "Magadan" to "Asia/Magadan",
                "Srednekolymsk" to "Asia/Srednekolymsk",
                "Solomon Is." to "Pacific/Guadalcanal",
                "New Caledonia" to "Pacific/Noumea",
                "Fiji" to "Pacific/Fiji",
                "Kamchatka" to "Asia/Kamchatka",
                "Marshall Is." to "Pacific/Majuro",
                "Auckland" to "Pacific/Auckland",
                "Wellington" to "Pacific/Auckland",
                "Nuku'alofa" to "Pacific/Tongatapu",
                "Tokelau Is." to "Pacific/Fakaofo",
                "Chatham Is." to "Pacific/Chatham",
                "Samoa" to "Pacific/Apia")
    }
}
