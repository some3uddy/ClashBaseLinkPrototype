package grid

import calculateCoordinatesIn

class CoordinateGrid {
    companion object {
        const val GRID_SIZE = 44
        val COORDINATE_RANGE = 0..<GRID_SIZE
    }

    private var _tiles: MutableList<MutableList<Tile>> = MutableList(GRID_SIZE) {
        MutableList(GRID_SIZE) { Tile.Empty }
    }

    val tiles: List<List<Tile>> get() = _tiles.map { it.toList() }.toList()

    override fun equals(other: Any?): Boolean {
        return other is CoordinateGrid && tiles == other.tiles
    }

    override fun hashCode(): Int {
        return tiles.hashCode()
    }

    //TODO check if used
    private fun getTile(coordinate: Coordinate): Tile = _tiles[coordinate.y][coordinate.x]
    private fun setTile(tile: Tile, coordinate: Coordinate) {
        _tiles[coordinate.y][coordinate.x] = tile
    }

    fun canPlaceBuilding(coordinate: Coordinate, size: Int) = getBlockedTileCoordinatesFor(coordinate, size) != null

    private fun getBlockedTileCoordinatesFor(originCoordinate: Coordinate, size: Int): List<Coordinate>? {
        val blockedTiles: List<Coordinate> = calculateCoordinatesIn(0..<size, originCoordinate)

        blockedTiles.forEach { coordinate ->
            if (coordinate.x !in COORDINATE_RANGE) {
                return null
            }
            if (coordinate.y !in COORDINATE_RANGE) {
                return null
            }
            if (getTile(coordinate) !is Tile.Empty) {
                return null
            }
        }

        return blockedTiles
    }

    fun populateRandomly() {
        _tiles = MutableList(GRID_SIZE) {
            MutableList(GRID_SIZE) { Tile.Empty }
        }

        val sizeToBuildingsSortedMap = BuildingType.buildings
            .shuffled()
            .groupBy { it.type.size }
            .toSortedMap(compareByDescending { it })

        sizeToBuildingsSortedMap.forEach { (size, buildings) ->
            val range = 0..<(GRID_SIZE - size + 1)

            for (building in buildings) {
                if (tryPlaceBuildingInRandomTile(range, building)) {
                    continue
                }
                if (tryPlaceBuildingInNextFreeTile(range, building)) {
                    continue
                }
                throw Error("Failed to place all buildings in grid.")
            }
        }

    }

    private fun tryPlaceBuildingInRandomTile(range: IntRange, building: Building): Boolean {
        var failCounter = 0
        while (failCounter < 500) {
            val randomCoordinate = Coordinate(range.random(), range.random())
            if (tryPlaceBuildingAt(building, randomCoordinate)) {
                return true
            }
            failCounter++
        }
        return false
    }

    private fun tryPlaceBuildingInNextFreeTile(range: IntRange, building: Building): Boolean {
        calculateCoordinatesIn(range).forEach {
            if (tryPlaceBuildingAt(building, it)) {
                return true
            }
        }
        return false
    }

    fun tryPlaceBuildingAt(building: Building, coordinate: Coordinate): Boolean {
        val futureBlockedCoordinates: MutableList<Coordinate> =
            getBlockedTileCoordinatesFor(coordinate, building.type.size)?.toMutableList()
                ?: return false

        val builtTile = Tile.Built(building)
        setTile(builtTile, futureBlockedCoordinates.removeFirst())
        val blockedTile = Tile.Blocked(builtTile)
        futureBlockedCoordinates.forEach { setTile(blockedTile, it) }

        return true
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


}