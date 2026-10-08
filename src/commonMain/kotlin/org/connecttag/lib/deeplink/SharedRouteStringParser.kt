package org.connecttag.lib.deeplink

public object SharedRouteStringParser {
    public fun parseInternalRoute(
        routeString: String?,
        packagePrefixes: Iterable<String> = emptyList(),
        normalizeRoute: (String) -> String? = { null },
    ): SharedRouteStringParts? {
        val value = cleanRouteString(routeString) ?: return null
        normalizeRoute(value)?.let { return SharedRouteStringParts(it) }
        val withoutPackagePrefix = packagePrefixes.fold(value) { route, prefix -> route.removePrefix(prefix) }
        normalizeRoute(withoutPackagePrefix)?.let { return SharedRouteStringParts(it) }
        return withoutPackagePrefix
            .takeIf { routePrefix(it).isNotBlank() }
            ?.let { SharedRouteStringParts(it) }
    }

    public fun parse(routeString: String?): SharedRouteStringParts? {
        return cleanRouteString(routeString)?.let { SharedRouteStringParts(it) }
    }

    public fun cleanRouteString(routeString: String?): String? {
        val value = routeString?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        if (value.contains('\n')) return null
        if (HttpUrlPolicy.isHttpUrl(value)) return null
        return value
    }

    public fun routePrefix(route: String): String {
        return route.routeStringPrefix()
    }

    public fun routeToken(value: String): String {
        return value.trim().toNormalizedToken()
    }
}
