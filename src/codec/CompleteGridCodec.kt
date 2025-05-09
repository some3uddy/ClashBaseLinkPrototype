package codec

import Coordinate
import CoordinateGrid
import Tile

class CompleteGridCodec : ICodec {

    override fun encodeGrid(grid: CoordinateGrid): String {
        return grid.tileList.joinToString("") { tile ->
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
        val buildingIds = binaryGrid.chunked(idBitSize).map { it.toInt(2) }.chunked(CoordinateGrid.SIZE)
        val grid = CoordinateGrid()

        buildingIds.forEachIndexed { y, row ->
            row.forEachIndexed { x, buildingId ->
                if (buildingId != 0b1111_1111_1111) {
                    val building = idToBuildingMap[buildingId]!!
                    val coordinate = Coordinate(x, y)
                    grid.tryAddBuilding(building, coordinate)
                }
            }
        }

        return grid
    }

}