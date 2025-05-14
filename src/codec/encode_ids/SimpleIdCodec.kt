package codec.encode_ids

import codec.core.BaseCodec
import grid.BuildingType
import grid.Coordinate
import grid.CoordinateGrid
import grid.Tile

class SimpleIdCodec(override val bitSize: Int) : BaseCodec("Simple ID codec (${bitSize}bit)") {

    override fun encodeGrid(grid: CoordinateGrid): String {
        return grid.tiles.flatten().joinToString("") { tile ->
            encodeIntToBinary(
                if (tile is Tile.Built) {
                    tile.building.id
                } else {
                    upperBound
                }
            )
        }
    }

    override fun decodeGrid(binaryGrid: String): CoordinateGrid {
        val buildingIds = binaryGrid.chunked(bitSize).map {
            decodeInt(it)
        }.chunked(CoordinateGrid.GRID_SIZE)

        val grid = CoordinateGrid()

        // TODO: write helper
        buildingIds.forEachIndexed { y, row ->
            row.forEachIndexed { x, buildingId ->
                if (buildingId != upperBound) {
                    val building = BuildingType.idToBuildingMap[buildingId]!!
                    val coordinate = Coordinate(x, y)
                    grid.tryPlaceBuildingAt(building, coordinate)
                }
            }
        }

        return grid
    }
}