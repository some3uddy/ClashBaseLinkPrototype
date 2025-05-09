import codec.CompleteGridCodec
import codec.ICodec
import codec.IgnoreBlockedCodec
import grid.Building
import grid.BuildingType
import grid.CoordinateGrid

// area is 44x44
// potentially use BitSet

// done:
// - basic encoder but not saving blocked slots at all
// - ignore blocked encoder with bool skip for empty

// todo:

// - ignore spaces too small
// - check notes
// - place from size 1 2 3 4 in that order, place 4 3 2 1 for mock

fun main() {
    runAll()
}

fun runAll() {
    println("complete")
    runExperiment(CompleteGridCodec())
    println()
    println("ignore blocked")
    runExperiment(IgnoreBlockedCodec())
//    println()
//    println("ignore blocked and too small")
//    runExperiment(IgnoreBlockedAndTooSmallCodec())
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