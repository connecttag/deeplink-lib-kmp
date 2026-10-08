package org.connecttag.lib.deeplink

public interface RouteAliasResolver {
    public fun resolve(rawRoute: String): String?
}

public object IdentityRouteAliasResolver : RouteAliasResolver {
    override fun resolve(rawRoute: String): String? =
        rawRoute.trim().takeIf { it.isNotEmpty() }
}
