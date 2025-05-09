class CoordinateGrid {
    companion object {
        const val SIZE = 44
        val COORDINATE_RANGE = 0..<SIZE
    }

    private val tiles: MutableList<MutableList<Tile>> = MutableList(SIZE) {
        MutableList(SIZE) { Tile.Empty }
    }

    val tileList: List<Tile>
        get() = tiles.flatten()

    override fun equals(other: Any?): Boolean {
        return other is CoordinateGrid && tileList == other.tileList
    }

    override fun hashCode(): Int {
        return tileList.hashCode()
    }

    fun compare(otherGrid: CoordinateGrid): Boolean {
        return tileList == otherGrid.tileList
//
//        tileList.zip(otherGrid.tileList).all { tileA ->
//        }

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


    fun tryAddBuilding(building: Building, coordinate: Coordinate): Boolean {
        val blockedCoordinates: List<Coordinate> =
            getBlockedTileCoordinates(coordinate, building.type.size)
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

//
//    fun tryAddBuilding(building: Building, coordinate: Coordinate): Boolean {
//        if (!isPlacementValid(building.type, coordinate)) {
//            return false
//        }
//
//        tiles[coordinate.y][coordinate.x] = building
//        return true
//    }


    private fun getBlockedTileCoordinates(coordinate: Coordinate, size: Int): List<Coordinate>? {
        val blockedTiles: MutableList<Coordinate> = mutableListOf()
        val blockedRange = 0..<size

        for (xOffset in blockedRange) {
            for (yOffset in blockedRange) {
                val x = coordinate.x + xOffset
                val y = coordinate.y + yOffset

                if (x !in COORDINATE_RANGE || y !in COORDINATE_RANGE) {
                    return null
                }

                val testedTile = tiles[y][x]

                if (testedTile is Tile.Blocked || testedTile is Tile.Built) {
                    return null
                }

                blockedTiles.add(Coordinate(x, y))
            }
        }

        return blockedTiles
    }


//    private fun isPlacementValid(buildingType: BuildingType, coordinate: Coordinate): Boolean {
//        if (coordinate.x !in COORDINATE_RANGE || coordinate.y !in COORDINATE_RANGE) {
//            return false
//        }
//
//        val blockedRange = 0..<buildingType.size
//
//        for (xOffset in blockedRange) {
//            for (yOffset in blockedRange) {
//                val x = coordinate.x + xOffset
//                val y = coordinate.y + yOffset
//
//                if (x !in COORDINATE_RANGE || y !in COORDINATE_RANGE) {
//                    return false
//                }
//
//                if (tiles[y][x] != null) {
//                    return false
//                }
//            }
//        }
//
//        return true
//    }
}