package org.connecttag.lib.deeplink

public object DeepLinkEngine {

    /**
     * Parses a raw URI string into a [DeepLinkResult].
     * Example: app://recharge?amount=500&id=123
     */
    public fun parseResult(uriString: String?): DeepLinkResult {
        if (uriString.isNullOrBlank()) return DeepLinkResult.Unrecognized

        return try {
            val schemeSplit = uriString.split("://")
            if (schemeSplit.size < 2) return DeepLinkResult.Unrecognized

            val content = schemeSplit[1]
            val querySplit = content.split("?")
            val hostAndPath = querySplit[0]
            val queryPart = if (querySplit.size > 1) querySplit[1] else ""

            val hostPathSplit = hostAndPath.split("/")
            val host = hostPathSplit[0]
            val path = if (hostPathSplit.size > 1) {
                "/" + hostPathSplit.drop(1).joinToString("/")
            } else {
                ""
            }

            val params = parseQueryParams(queryPart)

            DeepLinkResult.Success(
                DeepLinkAction(
                    host = host,
                    path = path,
                    params = params,
                ),
            )
        } catch (e: Exception) {
            DeepLinkResult.Error(e.message ?: "Unknown parsing error")
        }
    }

    public fun parse(uriString: String?): DeepLinkAction? {
        val result = parseResult(uriString)
        return if (result is DeepLinkResult.Success) result.action else null
    }

    private fun parseQueryParams(query: String): Map<String, String> {
        if (query.isBlank()) return emptyMap()

        return query.split("&")
            .filter { it.contains("=") }
            .associate {
                val parts = it.split("=", limit = 2)
                val key = parts[0]
                val value = if (parts.size > 1) decode(parts[1]) else ""
                key to value
            }
    }

    private fun decode(value: String): String {
        return RouteStringCodec.decodeRouteValue(value)
    }
}
