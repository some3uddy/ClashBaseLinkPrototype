import codec.CoordinatesPerIdCodec
import codec.ICodec
import grid.Building
import grid.BuildingType
import grid.CoordinateGrid

// area is 44x44


// done:
// - basic encoder but not saving blocked slots at all
// - ignore blocked encoder
// - ignore spaces too small
// - place in size order 4 3 2 1 for mock to randomize
// - bool skip for empty

// todo:
// - potentially test 2 bits to store how many fields are to be skipped? 
// - check notes

// reverse approach:
// - save coords in id order
// - save tiles to next id placement
//- start with smallest, save valid tiles to next id placement (how to reduce bitcount for skip number?)


fun main() {
    runAll()
}

fun runAll() {
//    println("complete")
//    runExperiment(CompleteGridCodec())
//    println()
//    println("ignore blocked")
//    runExperiment(IgnoreBlockedCodec())
//    println()
//    println("ignore blocked and too small")
//    runExperiment(IgnoreBlockedAndTooSmallCodec())
//    println()
//    println("skip invalid with bool")
//    runExperiment(SkipInvalidWithBoolCodec()) 
//    println()
    println("coordinates per id codec")
    runExperiment(CoordinatesPerIdCodec())
}

fun runExperiment(codec: ICodec) {
    val grid = CoordinateGrid.createRandomlyPopulated()

    val encodedGrid = codec.encodeGrid(grid)
    println("binary encoded length: ${encodedGrid.length}")
    val b64encodedGrid = ICodec.encodeBinaryToBase64(encodedGrid)
    println("base64 encoded size: ${b64encodedGrid.length}")

    val decodedFromb64Grid = ICodec.decodeBase64ToBinaryString(b64encodedGrid)
    println("binary decoded length: ${decodedFromb64Grid.length}")

    val decodedGrid = codec.decodeGrid(decodedFromb64Grid)
    //val decodedGrid = codec.decodeGrid(encodedGrid)

    val compressedBinaryString = ICodec.compressBinary(encodedGrid)
    println("compressed binary encoded length is ${compressedBinaryString.length}")

    println("equal: ${decodedGrid == grid}")

//    grid.print()
//    println()
//    decodedGrid.print()

}

fun getIdToBuildingMap(): Map<Int, Building> {
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