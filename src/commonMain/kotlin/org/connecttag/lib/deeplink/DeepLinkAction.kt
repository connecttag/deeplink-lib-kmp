package org.connecttag.lib.deeplink

/**
 * Represents a parsed deep link action result.
 */
public data class DeepLinkAction(
    val host: String,
    val path: String,
    val params: Map<String, String> = emptyMap(),
) {
    /**
     * Checks if the action matches a specific host and path pattern.
     */
    public fun matches(expectedHost: String, expectedPath: String): Boolean {
        return host.equals(expectedHost, ignoreCase = true) &&
            path.equals(expectedPath, ignoreCase = true)
    }

    public fun getParam(key: String): String? = params[key]

    public fun getIntParam(key: String): Int? = params[key]?.toIntOrNull()

    public fun getBooleanParam(key: String): Boolean = params[key]?.toBoolean() ?: false
}
