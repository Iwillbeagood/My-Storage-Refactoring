package com.example.mystorage.utils.etc

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Constants {
    const val TAG: String = "로그"
    const val IP_ADDRESS = "43.201.89.62"

    const val HEADER_ACCEPT = "accept: application/json"
    const val HEADER_CONTENT_TYPE = "content-type: application/x-www-form-urlencoded; charset=utf-8"

    const val SERVICE_ID_NAVER = "ncp:sms:kr:286601360175:mystorage"
    const val ACCESS_KEY_ID = "HQFBPMhzt4LrSbS6rr7D"
    const val SECRET_KEY = "e8BrJGHRyKzae3w3HFIFkb6WyQo39ijUbJsnWudj"

    fun getSignature(timestamp: Long): String? {
        val space = " " // one space
        val newLine = "\n" // new line
        val method = "POST" // method
        val url = "/sms/v2/services/$SERVICE_ID_NAVER/messages"

        val buffer = StringBuffer()
        buffer.append(method)
        buffer.append(space)
        buffer.append(url)
        buffer.append(newLine)
        buffer.append(timestamp)
        buffer.append(newLine)
        buffer.append(ACCESS_KEY_ID)
        println(buffer.toString())

        val key = SECRET_KEY.toByteArray()
        val signingKey = SecretKeySpec(key, "HmacSHA256")

        val bytes = buffer.toString().toByteArray(Charsets.UTF_8)
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)
        val digest = mac.doFinal(bytes)
        return android.util.Base64.encodeToString(digest, android.util.Base64.NO_WRAP)
    }
}
