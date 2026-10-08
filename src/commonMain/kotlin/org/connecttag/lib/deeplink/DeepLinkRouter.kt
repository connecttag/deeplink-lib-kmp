package org.connecttag.lib.deeplink

public fun interface DeepLinkHandler {
    public fun handle(action: DeepLinkAction): Boolean
}

public class DeepLinkRouter {
    private val routes = mutableMapOf<String, DeepLinkHandler>()

    /**
     * Registers a handler for a specific host and path.
     * format: "host/path" or "host"
     */
    public fun register(route: String, handler: DeepLinkHandler) {
        routes[route.lowercase()] = handler
    }

    public fun register(route: String, handler: (DeepLinkAction) -> Boolean) {
        register(route, DeepLinkHandler(handler))
    }

    public fun handle(uriString: String?): Boolean {
        val action = DeepLinkEngine.parse(uriString) ?: return false

        // Try exact match host + path
        val fullRoute = if (action.path.isNotBlank()) {
            "${action.host}${action.path}"
        } else {
            action.host
        }

        routes[fullRoute.lowercase()]?.let {
            if (it.handle(action)) return true
        }

        // Try host only match
        routes[action.host.lowercase()]?.let {
            if (it.handle(action)) return true
        }

        return false
    }
}
