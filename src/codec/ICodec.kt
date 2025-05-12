package codec

import grid.Building
import grid.BuildingType
import grid.CoordinateGrid
import java.util.*
import java.util.zip.Deflater


interface ICodec {

    companion object {
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
        fun decodeBase64ToBinaryString(base64: String): String {
            val byteArray = Base64.getUrlDecoder().decode(base64)
            return byteArray.joinToString("") {
                // toUByte(): makes it clear that the byte is unsigned
                // toString(2): converts bytes into base2/binary string
                // padStart(): ensures, that ints not using all bits are also 8bit
                it.toUByte().toString(2).padStart(8, '0')
            }
        }

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
    }

    val mask: Int
        get() = 0b1111_1111_1111
    val idBitSize: Int
        get() = 12

    private val idRange: IntRange
        get() = 0..mask

    fun encodeToUrlSafeBase64() {
        //https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.io.encoding/-base64/-default/-url-safe.html

    }


    val idToBuildingMap: Map<Int, Building>
        get() {
            val buildings: MutableMap<Int, Building> = mutableMapOf()
            var idCounter = 0

            BuildingType.entries.forEach { buildingType ->
                repeat(buildingType.amount) {
                    val newBuilding = Building(idCounter, buildingType)
                    buildings.put(idCounter, newBuilding)
                    idCounter++
                }
            }

            return buildings.toMap()
        }

    fun encodeBuildingId(id: Int): String {
        require(id in idRange)
        return id.toString(2).padStart(idBitSize, '0')
    }

    fun decodeBuildingId(binaryId: String): Int {
        require(binaryId.length == idBitSize)
        return binaryId.toInt(2)
    }

    fun encodeGrid(grid: CoordinateGrid): String
    fun decodeGrid(binaryGrid: String): CoordinateGrid

}