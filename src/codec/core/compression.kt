package codec.core

import java.util.zip.Deflater

fun compressBinary(input: String): String {
    val lengthAfterPadding = (input.length + 7) / 8 * 8
    val binaryStringPadded = input.padEnd(lengthAfterPadding, '0')
    val byteArray = binaryStringPadded.chunked(8).map {
        it.toInt(2).toByte()
    }.toByteArray()

    val deflater = Deflater()
    deflater.setInput(byteArray)
    deflater.finish()

    val output = ByteArray(4096)
    val compressedSize = deflater.deflate(output)
    val compressed = output.copyOf(compressedSize)

    return compressed.joinToString("") {
        // toUByte(): makes it clear that the byte is unsigned
        // toString(2): converts bytes into base2/binary string
        // padStart(): ensures, that ints not using all bits are also 8bit
        it.toUByte().toString(2).padStart(8, '0')
    }
}