package org.connecttag.lib.deeplink

internal object UriComponentCodec {
    private const val SPACE_CODE = 0x20
    private val HEX_DIGITS = "0123456789ABCDEF".toCharArray()

    private fun Int.isUrlFormSafeByte(): Boolean =
        (this in 0x61..0x7A) || // a-z
        (this in 0x41..0x5A) || // A-Z
        (this in 0x30..0x39) || // 0-9
        this == 0x2D ||         // -
        this == 0x2E ||         // .
        this == 0x5F ||         // _
        this == 0x2A            // *

    fun encodeQueryComponent(value: String): String {
        val bytes = value.encodeToByteArray()
        return buildString {
            bytes.forEach { byte ->
                val code = byte.toInt() and 0xFF
                when {
                    code == SPACE_CODE -> append('+')
                    code.isUrlFormSafeByte() -> append(code.toChar())
                    else -> {
                        append('%')
                        append(HEX_DIGITS[code ushr 4])
                        append(HEX_DIGITS[code and 0x0F])
                    }
                }
            }
        }
    }
}

internal object HttpUrlPolicy {
    fun isHttpUrl(value: String?): Boolean {
        val cleaned = value.cleanUrlCandidate() ?: return false
        if (cleaned.hasUriControlCharacters()) return false
        return cleaned.startsWith("http://", ignoreCase = true) ||
            cleaned.startsWith("https://", ignoreCase = true)
    }

    fun isHttpsUrl(value: String?): Boolean {
        val cleaned = value.cleanUrlCandidate() ?: return false
        if (cleaned.hasUriControlCharacters()) return false
        return cleaned.startsWith("https://", ignoreCase = true)
    }

    fun normalizeHttpUrl(value: String?): String? {
        val cleaned = value.cleanUrlCandidate() ?: return null
        return cleaned.takeIf { isHttpUrl(it) }
    }

    private fun String?.cleanUrlCandidate(): String? {
        val raw = this ?: return null
        if (raw.hasUriControlCharacters()) return null
        return raw.trim().takeIf { it.isNotEmpty() }
    }
}

internal fun String.hasUriControlCharacters(): Boolean =
    any { character -> character.code < 0x20 || character.code == 0x7F }

private val lowerToUpperBoundary = Regex("([a-z\\d])([A-Z])")
private val nonAsciiTokenCharacters = Regex("[^A-Za-z0-9]+")

internal fun String.toNormalizedToken(): String =
    replace(lowerToUpperBoundary, "$1_$2")
        .replace(nonAsciiTokenCharacters, "_")
        .trim('_')
        .uppercase()

internal fun String?.toNormalizedTokenOrNull(): String? =
    this?.trim()
        ?.takeIf(String::isNotEmpty)
        ?.toNormalizedToken()
        ?.takeIf(String::isNotEmpty)
