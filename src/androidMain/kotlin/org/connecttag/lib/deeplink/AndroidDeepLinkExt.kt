package org.connecttag.lib.deeplink

import android.content.Intent

/**
 * Android-specific extensions for handling deep links via Intents.
 */
public fun DeepLinkEngine.parse(intent: Intent?): DeepLinkAction? {
    return parse(intent?.dataString)
}

public fun DeepLinkEngine.parseResult(intent: Intent?): DeepLinkResult {
    return parseResult(intent?.dataString)
}

public fun DeepLinkRouter.handle(intent: Intent?): Boolean {
    return handle(intent?.dataString)
}
