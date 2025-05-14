package codec.core

import grid.CoordinateGrid
import kotlin.math.pow

abstract class BaseCodec(val name: String) {

    protected abstract val bitSize: Int
    protected val upperBound: Int by lazy { 2.0.pow(bitSize).toInt() - 1 }

    protected fun encodeIntToBinary(int: Int): String {
        require(int in 0..upperBound)
        return int.toString(2).padStart(bitSize, '0')
    }

    protected fun decodeInt(binaryInt: String): Int {
        require(binaryInt.length == bitSize)
        return binaryInt.toInt(2)
    }

    protected abstract fun encodeGrid(grid: CoordinateGrid): String
    protected abstract fun decodeGrid(binaryGrid: String): CoordinateGrid

    //TODO: also return lenghts to take averages
    fun testEncoding(
        printOutput: Boolean = false,
        printGrid: Boolean = false,
        printGridOnFail: Boolean = true
    ): Boolean {
        val grid = CoordinateGrid()
        grid.populateRandomly()

        val binaryEncodedGrid: String = encodeGrid(grid)
        val base64EncodedGrid: String = encodeBinaryToBase64(binaryEncodedGrid)
        val binaryDecodedGrid: String = decodeBase64ToBinary(base64EncodedGrid)
        val decodedGrid: CoordinateGrid = decodeGrid(binaryDecodedGrid)

        val wasEncodingSuccessful = grid == decodedGrid

        if (printOutput) {
            println("binary encoded length: ${binaryEncodedGrid.length}")
            println("base64 encoded size: ${base64EncodedGrid.length}")
            println("encoding successful: $wasEncodingSuccessful")
        }

        if (printGrid || printGridOnFail && !wasEncodingSuccessful) {
            println("grid:")
            grid.print()
            println("decoded grid:")
            decodedGrid.print()
        }

        if (!wasEncodingSuccessful) {
            println("encoding failed")
        }

        return wasEncodingSuccessful
    }
}