package org.connecttag.lib.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DeepLinkEngineTest {

    @Test
    fun parseResultValidDeepLink() {
        val uri = "app://payment/checkout?amount=250&currency=YER&success=true"
        val result = DeepLinkEngine.parseResult(uri)

        assertTrue(result is DeepLinkResult.Success)
        val action = result.action
        assertEquals("payment", action.host)
        assertEquals("/checkout", action.path)
        assertEquals("250", action.getParam("amount"))
        assertEquals(250, action.getIntParam("amount"))
        assertEquals("YER", action.getParam("currency"))
        assertEquals(true, action.getBooleanParam("success"))
    }

    @Test
    fun parseResultRootPath() {
        val uri = "myapp://profile?userId=42"
        val action = DeepLinkEngine.parse(uri)

        assertNotNull(action)
        assertEquals("profile", action.host)
        assertEquals("", action.path)
        assertEquals("42", action.getParam("userId"))
        assertEquals(42, action.getIntParam("userId"))
    }

    @Test
    fun matchesHostAndPath() {
        val action = DeepLinkAction(
            host = "store",
            path = "/items",
            params = mapOf("category" to "electronics"),
        )

        assertTrue(action.matches("store", "/items"))
        assertTrue(action.matches("STORE", "/ITEMS"))
        assertEquals(false, action.matches("store", "/orders"))
    }
}
