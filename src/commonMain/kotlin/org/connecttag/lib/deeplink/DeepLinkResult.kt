package org.connecttag.lib.deeplink

public sealed interface DeepLinkResult {
    public data class Success(val action: DeepLinkAction) : DeepLinkResult
    public data object Unrecognized : DeepLinkResult
    public data class Error(val message: String) : DeepLinkResult
}
