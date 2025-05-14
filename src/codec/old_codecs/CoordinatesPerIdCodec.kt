//package codec.old_codecs
//
//import codec.core.ICodec
//import grid.Coordinate
//import grid.CoordinateGrid
//import grid.Tile
//
//class CoordinatesPerIdCodec : ICodec {
//    val bitSize = 11
//
//    private fun coordinateToIndex(coordinate: Coordinate): Int {
//        return coordinate.y * CoordinateGrid.GRID_SIZE + coordinate.x
//    }
//
//    private fun indexToCoordinate(index: Int): Coordinate {
//        val x = index % CoordinateGrid.GRID_SIZE
//        val y = index / CoordinateGrid.GRID_SIZE
//        return Coordinate(x, y)
//    }
//
//
//    override fun encodeGrid(grid: CoordinateGrid): String {
//
//        val idToCoordinateMap: MutableMap<Int, Coordinate> = mutableMapOf()
//
//
//        grid.tiles.forEachIndexed { y, row ->
//            row.forEachIndexed { x, tile ->
//                if (tile is Tile.Built) {
//                    idToCoordinateMap[tile.building.id] = Coordinate(x, y)
//                }
//            }
//        }
//
//        return getIdToBuildingMap().values.sortedBy { it.id }.joinToString("") {
//            val coordinate = idToCoordinateMap[it.id]
//
//            if (coordinate == null) {
//                println(it.id)
//                return@joinToString ""
//            }
//
//            val index = coordinateToIndex(coordinate)
//            require(index in 0..<0b1111_1111_111)
//            return@joinToString index.toString(2).padStart(11, '0')
//        }
//    }
//
//    override fun decodeGrid(binaryGrid: String): CoordinateGrid {
//
//        val coordinates = binaryGrid
//            .chunked(11)
//            .map { it.toInt(2) }
//            .map { indexToCoordinate(it) }
//
//        val grid = CoordinateGrid()
//
//        coordinates.zip(
//            getIdToBuildingMap().values.sortedBy { it.id }
//        ).forEach { (coordinate, building) ->
//            grid.tryAddBuilding(building, coordinate)
//        }
//
//        return grid
//    }
//
//
//}