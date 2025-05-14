package codec.core

import java.util.*

fun encodeBinaryToBase64(binary: String): String {
    val lengthAfterPadding = (binary.length + 7) / 8 * 8
    val binaryStringPadded = binary.padEnd(lengthAfterPadding, '0')
    val byteArray = binaryStringPadded.chunked(8).map {
        it.toInt(2).toByte()
    }.toByteArray()

    // experimental atm
    //Base64.UrlSafe.encode(byteArray)
    return Base64.getUrlEncoder().encodeToString(byteArray)
}

// TODO: fix padding at the end
fun decodeBase64ToBinary(base64: String): String {
    val byteArray = Base64.getUrlDecoder().decode(base64)
    return byteArray.joinToString("") {
        // toUByte(): makes it clear that the byte is unsigned
        // toString(2): converts bytes into base2/binary string
        // padStart(): ensures, that ints not using all bits are also 8bit
        it.toUByte().toString(2).padStart(8, '0')
    }
}