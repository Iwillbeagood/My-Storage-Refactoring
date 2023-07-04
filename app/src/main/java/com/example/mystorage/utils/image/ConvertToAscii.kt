package com.example.mystorage.utils.image

object ConvertToAscii {
    fun convertToAscii(s: String): String {
        val result = StringBuilder()
        for (c in s) {
            val codePoint = c.code
            if (codePoint in 0x0000..0x007F) {
                result.append(c)
            } else {
                result.append(String.format("\\u%04X", codePoint))
            }
        }
        return result.toString()
    }

}