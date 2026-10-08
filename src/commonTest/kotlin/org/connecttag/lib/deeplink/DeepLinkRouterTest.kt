package org.connecttag.lib.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeepLinkRouterTest {

    @Test
    fun routerDispatchesToMatchingHandler() {
        val router = DeepLinkRouter()
        var checkoutHandled = false
        var profileHandled = false

        router.register("payment/checkout") { action ->
            checkoutHandled = true
            assertEquals("500", action.getParam("amount"))
            true
        }

        router.register("profile") { _ ->
            profileHandled = true
            true
        }

        val handledCheckout = router.handle("app://payment/checkout?amount=500")
        assertTrue(handledCheckout)
        assertTrue(checkoutHandled)

        val handledProfile = router.handle("app://profile?id=9")
        assertTrue(handledProfile)
        assertTrue(profileHandled)
    }
}
