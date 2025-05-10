package codec

import getIdToBuildingMap
import grid.Building
import grid.Coordinate
import grid.CoordinateGrid
import grid.Tile

class IgnoreBlockedAndTooSmallCodec : ICodec {
    // skips blocked tiles in the encoding, as well as tiles that don't fit 
    // (size 2=< reaches a blocked tile or border)

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
        val emptyBuildingIdBinaryString = encodeBuildingId(0b1111_1111_1111)

        while (builtAndEmptyCoordinateTilePairs.isNotEmpty()) {
            val coordinateTilePair = builtAndEmptyCoordinateTilePairs.removeFirst()
            val currentCoordinate = coordinateTilePair.first
            val currentTile = coordinateTilePair.second

            if (currentTile is Tile.Built) {
                val binaryBuildingId = encodeBuildingId(currentTile.building.id)
                binaryStringBuilder.append(binaryBuildingId)
                reconstructedGrid.tryAddBuilding(currentTile.building, currentCoordinate)
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
        val idToBuildingMap: MutableMap<Int, Building> = getIdToBuildingMap().toMutableMap()

        val buildingsOrEmpty: MutableList<Building?> = binaryGrid
            .chunked(idBitSize)
            .map {
                val id = it.toInt(2)
                //idToBuildingMap[id]
                // remove so the padded 0s at the end don't count
                idToBuildingMap.remove(id)
            }
            .toMutableList()

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
                    if (!grid.tryAddBuilding(currentBuilding, currentCoordinate)) {
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