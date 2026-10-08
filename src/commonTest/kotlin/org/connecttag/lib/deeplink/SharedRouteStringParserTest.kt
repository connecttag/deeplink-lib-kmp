package org.connecttag.lib.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SharedRouteStringParserTest {
    @Test
    fun readsQueryPathAndConstructorStyleValues() {
        assertEquals(
            "123",
            SharedRouteStringParser.parseInternalRoute("Detail?id=123&entityType=PRODUCT")
                ?.routeId("id", "Detail"),
        )
        assertEquals(
            "server-1",
            SharedRouteStringParser.parseInternalRoute("NotificationDetail/server-1")
                ?.routeId("id", "NotificationDetail"),
        )
        assertEquals(
            "CART",
            SharedRouteStringParser.parseInternalRoute("Login(returnto=CART)")
                ?.value("returnTo"),
        )
    }

    @Test
    fun stripsKnownPackagePrefixesBeforeResolvingPrefix() {
        val parsed = SharedRouteStringParser.parseInternalRoute(
            routeString = "com.example.product.feature.account.navigation.AccountRoute.AddressEdit(addressId=a1)",
            packagePrefixes = listOf("com.example.product.feature.account.navigation.AccountRoute."),
        )

        assertEquals("AddressEdit", parsed?.prefix)
        assertEquals("a1", parsed?.value("addressId"))
    }

    @Test
    fun normalizesThroughCallerProvidedResolver() {
        val parsed = SharedRouteStringParser.parseInternalRoute(
            routeString = "المفضلة",
            normalizeRoute = { if (it == "المفضلة") "Favorite" else null },
        )

        assertEquals("Favorite", parsed?.prefix)
    }

    @Test
    fun rejectsExternalUrlsAndMultilineRouteStrings() {
        assertNull(SharedRouteStringParser.parseInternalRoute("https://example.com"))
        assertNull(SharedRouteStringParser.parseInternalRoute("Home\nFavorite"))
    }
}
