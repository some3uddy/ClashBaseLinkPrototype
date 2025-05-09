package codec

import Building
import Coordinate
import CoordinateGrid
import Tile
import getIdToBuildingMap

class IgnoreBlockedAndTooSmallCodec : ICodec {

    override fun encodeGrid(grid: CoordinateGrid): String {
        return grid.tileList.filter { it !is Tile.Blocked }.joinToString("") { tile ->
            encodeBuildingId(
                if (tile is Tile.Built) {
                    tile.building.id
                } else {
                    0b1111_1111_1111
                }
            )
        }
    }

    override fun decodeGrid(binaryGrid: String): CoordinateGrid {
        val idToBuildingMap: Map<Int, Building> = getIdToBuildingMap()

        val buildingIds = binaryGrid
            .chunked(idBitSize)
            .mapNotNull {
                val id = it.toInt(2)
                idToBuildingMap[id]
            }
            .iterator()
        //.chunked(CoordinateGrid.SIZE)

        val grid = CoordinateGrid()
        var currentBuilding = buildingIds.next()

        outer@ for (y in CoordinateGrid.COORDINATE_RANGE) {
            for (x in CoordinateGrid.COORDINATE_RANGE) {
                if (!grid.tryAddBuilding(currentBuilding, Coordinate(x, y))) {
                    continue
                }

                if (!buildingIds.hasNext()) {
                    break@outer
                }

                currentBuilding = buildingIds.next()
            }
        }
        return grid
    }

}