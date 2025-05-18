package codec.encode_ids

import calculateCoordinatesIn
import codec.core.BaseCodec
import grid.*

class ValidTilesIdCodec(override val bitSize: Int) : BaseCodec("Valid tiles ID codec (${bitSize}bit)") {

    override fun encodeGrid(grid: CoordinateGrid): String {
        val builtAndEmptyCoordinateTilePairs: MutableList<Pair<Coordinate, Tile>> = mutableListOf()

        grid.tiles.forEachIndexed { y, row ->
            row.forEachIndexed { x, tile ->
                if (tile !is Tile.Blocked) {
                    builtAndEmptyCoordinateTilePairs.add(Pair(Coordinate(x, y), tile))
                }
            }
        }

        val reconstructedGrid = CoordinateGrid()
        val binaryStringBuilder = StringBuilder()
        val emptyBuildingIdBinaryString = encodeIntToBinary(upperBound)

        while (builtAndEmptyCoordinateTilePairs.isNotEmpty()) {
            val coordinateTilePair = builtAndEmptyCoordinateTilePairs.removeFirst()
            val currentCoordinate = coordinateTilePair.first
            val currentTile = coordinateTilePair.second

            if (currentTile is Tile.Built) {
                val binaryBuildingId = encodeIntToBinary(currentTile.building.id)
                binaryStringBuilder.append(binaryBuildingId)
                reconstructedGrid.tryPlaceBuildingAt(currentTile.building, currentCoordinate)
                continue
            }

            val nextBuiltTile = builtAndEmptyCoordinateTilePairs.firstNotNullOfOrNull {
                it.second as? Tile.Built
            } ?: break

            val nextBuildingSize = nextBuiltTile.building.type.size

            if (reconstructedGrid.canPlaceBuilding(currentCoordinate, nextBuildingSize)) {
                binaryStringBuilder.append(emptyBuildingIdBinaryString)
            }
        }

        return binaryStringBuilder.toString()
    }

    override fun decodeGrid(binaryGrid: String): CoordinateGrid {
        //TODO: make immutable
        val idToBuildingMap: MutableMap<Int, Building> = BuildingType.idToBuildingMap.toMutableMap()

        val buildingsOrEmpty: MutableList<Building?> = binaryGrid
            .chunked(bitSize)
            .filter { it.length == bitSize } // remove potential padding
            .map { idToBuildingMap[decodeInt(it)] }
            .toMutableList()

        val grid = CoordinateGrid()
        var currentBuilding = buildingsOrEmpty.removeFirstOrNull()

        //TODO: cleanup
        for (coordinate in calculateCoordinatesIn(CoordinateGrid.COORDINATE_RANGE)) {

            if (currentBuilding != null) {
                // if current is a building, try to place it in the next valid tile
                if (!grid.tryPlaceBuildingAt(currentBuilding, coordinate)) {
                    // resume with same building, if next building couldn't be placed
                    continue
                }
            } else {
                // if not, take the next building and skip until it could be placed, 
                // then skip all empty (null) tiles
                val nextBuilding = buildingsOrEmpty.firstOrNull { it != null }
                    ?: break

                if (!grid.canPlaceBuilding(coordinate, nextBuilding.type.size)) {
                    // resume with same empty, if next building couldn't be placed
                    continue
                }
            }

            // if none are left this will be null and the loop is terminated next run
            currentBuilding = buildingsOrEmpty.removeFirstOrNull()
        }

        return grid
    }


}