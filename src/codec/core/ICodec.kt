package codec.core

import grid.Building
import grid.BuildingType
import grid.CoordinateGrid


interface ICodec {

//    val mask: Int
//        get() = 0b1111_1111_1111
//    val idBitSize: Int
//        get() = 12
//
//    private val idRange: IntRange
//        get() = 0..mask

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


    fun encodeGrid(grid: CoordinateGrid): String
    fun decodeGrid(binaryGrid: String): CoordinateGrid

}