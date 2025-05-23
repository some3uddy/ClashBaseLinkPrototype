package codec.encode_ids

import codec.core.BaseCodec
import grid.*

class ValidTilesSkipEmptyIdCodec(override val bitSize: Int) : BaseCodec("Skip invalid tiles ID codec (${bitSize}bit)") {

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

        while (builtAndEmptyCoordinateTilePairs.isNotEmpty()) {
            val coordinateTilePair = builtAndEmptyCoordinateTilePairs.removeFirst()
            val currentCoordinate = coordinateTilePair.first
            val currentTile = coordinateTilePair.second

            if (currentTile is Tile.Built) {
                val binaryBuildingId = encodeIntToBinary(currentTile.building.id)
                binaryStringBuilder.append("1")
                binaryStringBuilder.append(binaryBuildingId)

                reconstructedGrid.tryPlaceBuildingAt(currentTile.building, currentCoordinate)
                continue
            }

            val nextBuiltTile = builtAndEmptyCoordinateTilePairs.firstNotNullOfOrNull {
                it.second as? Tile.Built
            } ?: break

            val nextBuildingSize = nextBuiltTile.building.type.size

            if (reconstructedGrid.canPlaceBuilding(currentCoordinate, nextBuildingSize)) {
                binaryStringBuilder.append("0")
            }
        }

        return binaryStringBuilder.toString()
    }

    override fun decodeGrid(binaryGrid: String): CoordinateGrid {
        val idToBuildingMap: MutableMap<Int, Building> = BuildingType.idToBuildingMap.toMutableMap()
        val buildingsOrEmpty: MutableList<Building?> = mutableListOf()
        val binaryGridStringBuilder = StringBuilder(binaryGrid)

        while (binaryGridStringBuilder.isNotEmpty() && binaryGridStringBuilder.length >= bitSize + 1) {
            if (binaryGridStringBuilder.first() == '0') {
                buildingsOrEmpty.add(null)
                binaryGridStringBuilder.deleteCharAt(0)
                continue
            }

            val nextBuildingId = binaryGridStringBuilder.substring(1, bitSize + 1).toInt(2)
            // remove so the padded 0s at the end don't count
            buildingsOrEmpty.add(idToBuildingMap.remove(nextBuildingId))
            binaryGridStringBuilder.deleteRange(0, bitSize + 1)
        }

        val grid = CoordinateGrid()
        var currentBuilding = buildingsOrEmpty.removeFirstOrNull()

        outer@ for (y in CoordinateGrid.COORDINATE_RANGE) {
            for (x in CoordinateGrid.COORDINATE_RANGE) {

                // if not null, check if can place or skip, dont change to next item
                // if null then check if next could even be placed here

                val currentCoordinate = Coordinate(x, y)

                if (currentBuilding == null) {
                    // empty tile, try if next building could be placed, else skip
                    val nextBuilding = buildingsOrEmpty.firstOrNull { it != null }
                        ?: break@outer
                    val nextBuildingSize = nextBuilding.type.size

                    if (!grid.canPlaceBuilding(currentCoordinate, nextBuildingSize)) {
                        continue
                    }
                } else {
                    // tile with building, try if it can be placed, else skip
                    if (!grid.tryPlaceBuildingAt(currentBuilding, currentCoordinate)) {
                        continue
                    }
                }

                // can be null (like empty) because loop is terminated, if there's no buildings left
                currentBuilding = buildingsOrEmpty.removeFirstOrNull()
            }

        }

        return grid
    }
}