package org.connecttag.lib.deeplink

public data class SharedRouteStringParts(
    public val route: String,
) {
    public val prefix: String
        get() = SharedRouteStringParser.routePrefix(route)

    public fun routeId(queryKey: String, pathPrefix: String): String? {
        return value(queryKey) ?: pathValueAfter(pathPrefix)
    }

    public fun value(name: String): String? {
        route.queryRouteValue(name)?.let { return it }
        return route.argumentRouteValue(name)
    }

    public fun pathValueAfter(prefix: String): String? {
        val marker = "$prefix/"
        if (!route.contains(marker)) return null
        return route.substringAfter(marker)
            .substringBefore("?")
            .substringBefore("&")
            .cleanRouteValue()
    }

    public fun booleanValue(name: String): Boolean? = value(name)?.toRouteBoolean()

    public fun pathBooleanAfter(prefix: String): Boolean? {
        return pathValueAfter(prefix)?.toRouteBoolean()
    }
}
