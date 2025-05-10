package grid

import getIdToBuildingMap

class CoordinateGrid {
    companion object {
        const val GRID_SIZE = 44
        val COORDINATE_RANGE = 0..<GRID_SIZE

        fun createRandomlyPopulated(): CoordinateGrid {
            val grid = CoordinateGrid()
            grid.populateRandomly()
            return grid
        }
    }

    var tiles: MutableList<MutableList<Tile>> = MutableList(GRID_SIZE) {
        MutableList(GRID_SIZE) { Tile.Empty }
    }

    fun isBlocked(coordinate: Coordinate): Boolean = tiles[coordinate.y][coordinate.x] is Tile.Blocked

    val tileList: List<Tile>
        get() = tiles.flatten()

    override fun equals(other: Any?): Boolean {
        return other is CoordinateGrid && tileList == other.tileList
    }

    override fun hashCode(): Int {
        return tileList.hashCode()
    }

    fun print() {
        tiles.forEach { row ->
            println(row.map { tile ->
                when (tile) {
                    Tile.Empty -> 0
                    is Tile.Built -> tile.building.type.size
                    is Tile.Blocked -> tile.source.building.type.size
                }
            })
        }
    }

    fun isTileEmpty(coordinate: Coordinate) = tiles[coordinate.y][coordinate.x] is Tile.Empty
    fun canPlaceBuilding(coordinate: Coordinate, size: Int) = getBlockedTileCoordinatesFor(coordinate, size) != null

    fun tryAddBuilding(building: Building, coordinate: Coordinate): Boolean {
        val blockedCoordinates: List<Coordinate> =
            getBlockedTileCoordinatesFor(coordinate, building.type.size)
                ?: return false

        fun setTile(coordinate: Coordinate, tile: Tile) {
            tiles[coordinate.y][coordinate.x] = tile
        }

        val builtTile = Tile.Built(building)
        val blockedTile = Tile.Blocked(builtTile)
        setTile(blockedCoordinates[0], builtTile)

        for (coordinate in blockedCoordinates.drop(1)) {
            setTile(coordinate, blockedTile)
        }

        return true
    }

    private fun getBlockedTileCoordinatesFor(coordinate: Coordinate, size: Int): List<Coordinate>? {
        val blockedTiles: MutableList<Coordinate> = mutableListOf()
        val blockedRange = 0..<size

        for (xOffset in blockedRange) {
            for (yOffset in blockedRange) {
                val x = coordinate.x + xOffset
                val y = coordinate.y + yOffset

                if (x !in COORDINATE_RANGE || y !in COORDINATE_RANGE) {
                    return null
                }

                val newCoordinate = Coordinate(x, y)

                if (!isTileEmpty(newCoordinate)) {
                    return null
                }

                blockedTiles.add(newCoordinate)
            }
        }

        return blockedTiles
    }

    fun populateRandomly() {
        tiles = MutableList(GRID_SIZE) {
            MutableList(GRID_SIZE) { Tile.Empty }
        }

        val buildingsBySize = getIdToBuildingMap()
            // all buildings
            .values
            //  shuffled
            .shuffled()
            // grouped by size
            .groupBy { it.type.size }
            // as list of Map.Entry
            .entries
            // sorted by largest to smallest
            .sortedByDescending { it.key }
            // as list of (Size, Lists of buildings) Pair 
            .map { Pair(it.key, it.value) }

        buildingsBySize.forEach { (size, buildings) ->
            placeBuildingsOfSizeRandomly(size, buildings.iterator())
        }

    }

    private fun placeBuildingsOfSizeRandomly(size: Int, buildings: Iterator<Building>) {
        val range = 0..<(GRID_SIZE - size + 1)
        var building = buildings.next()
        var failCounter = 0

        do {
            val randomCoordinate = Coordinate(range.random(), range.random())
            if (!tryAddBuilding(building, randomCoordinate)) {
                failCounter++

                if (failCounter >= 500) {
                    println("resort to manual placing")
                    do {
                        if (!placeBuildingInNextFree(range, building)) {
                            println("failed to place all buildings of size: $size")
                            return
                        }
                        building = buildings.next()
                    } while (buildings.hasNext())
                    return
                }
                continue
            }

            failCounter = 0
            building = buildings.next()
        } while (buildings.hasNext())
    }

    fun placeBuildingInNextFree(range: IntRange, building: Building): Boolean {
        outer@ for (y in range) {
            for (x in range) {
                if (tryAddBuilding(building, Coordinate(x, y))) {
                    return true
                }
            }
        }
        return false
    }

}