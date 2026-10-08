package org.connecttag.lib.deeplink

/**
 * Shared helper for route query encoding/decoding.
 *
 * Keeping encoding helpers here ensures all modules parsing navigation route strings use the same rules.
 */
public object RouteStringCodec {
    public fun buildRoute(routeKey: String, vararg params: Pair<String, String?>): String {
        val key = routeKey.trim()
        require(key.isNotBlank()) { "routeKey must not be blank" }

        val query = params
            .mapNotNull { (name, value) ->
                val normalizedName = name.trim().takeIf { it.isNotEmpty() } ?: return@mapNotNull null
                value?.let { "${normalizeAndEncode(normalizedName)}=${encodeRouteValue(it)}" }
            }
            .joinToString("&")

        return if (query.isEmpty()) key else "$key?$query"
    }

    public fun routePrefix(route: String): String =
        routePrefixValue(route)

    public fun queryValue(route: String, name: String): String? =
        route.queryRouteValue(name)

    public fun argumentValue(route: String, name: String): String? =
        route.argumentRouteValue(name)

    public fun value(route: String, name: String): String? =
        queryValue(route, name) ?: argumentValue(route, name)

    public fun toRouteBoolean(value: String): Boolean? = when (value.trim().lowercase()) {
        "true", "1", "yes" -> true
        "false", "0", "no" -> false
        else -> null
    }

    public fun encodeRouteValue(value: String): String = normalizeAndEncode(value)

    public fun decodeRouteValue(value: String): String {
        val normalized = value
            .replace("+", " ")
            .replace("%20", " ")
        return normalized.percentDecode().trim().takeIf { it.isNotBlank() } ?: normalized
    }

    private fun normalizeAndEncode(value: String): String {
        if (!value.any { it == ' ' || it == '+' || it == '%' || it.code < 0x20 || it.code == 0x7F }) {
            return UriComponentCodec.encodeQueryComponent(value)
        }

        return UriComponentCodec.encodeQueryComponent(
            value.replace("+", "%2B"),
        )
    }

    private fun String.percentDecode(): String {
        if ('%' !in this && '+' !in this) return this
        val byteBuffer = mutableListOf<Byte>()
        val result = StringBuilder()

        fun flushBytes() {
            if (byteBuffer.isEmpty()) return
            result.append(byteBuffer.toByteArray().decodeToString())
            byteBuffer.clear()
        }

        var index = 0
        while (index < length) {
            when (val char = this[index]) {
                '%' -> {
                    if (index + 2 >= length) return this
                    val high = this[index + 1].digitToIntOrNull(16) ?: return this
                    val low = this[index + 2].digitToIntOrNull(16) ?: return this
                    byteBuffer += ((high shl 4) + low).toByte()
                    index += 3
                }

                '+' -> {
                    flushBytes()
                    result.append(' ')
                    index += 1
                }

                else -> {
                    flushBytes()
                    result.append(char)
                    index += 1
                }
            }
        }
        flushBytes()
        return result.toString()
    }
}

public fun String.routeStringPrefix(): String = routePrefixValue(this)

public fun String.queryRouteValue(name: String): String? {
    val query = substringAfter("?", missingDelimiterValue = "")
    if (query.isBlank()) return null
    return query.split("&")
        .firstOrNull { part -> part.substringBefore("=").equals(name, ignoreCase = true) }
        ?.substringAfter("=", missingDelimiterValue = "")
        ?.cleanRouteValue()
}

public fun String?.cleanRouteValue(): String? =
    this?.let { RouteStringCodec.decodeRouteValue(it).takeIf { value -> value.isNotBlank() } }

public fun String.argumentRouteValue(name: String): String? {
    val match = Regex("""(?i)(?:^|[?&,(])\s*${Regex.escape(name)}\s*=\s*""")
        .find(this)
        ?: return null
    return substring(match.range.last + 1)
        .substringBefore(")")
        .substringBefore(",")
        .cleanRouteValue()
}

public fun String.toRouteBoolean(): Boolean? = RouteStringCodec.toRouteBoolean(this)

private fun routePrefixValue(route: String): String =
    route.substringBefore("?")
        .substringBefore("/")
        .substringBefore("&")
        .substringBefore("(")
        .trim()
