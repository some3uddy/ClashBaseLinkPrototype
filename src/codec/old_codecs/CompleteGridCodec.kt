//package codec.old_codecs
//
//import codec.core.ICodec
//import grid.Coordinate
//import grid.CoordinateGrid
//import grid.Tile
//
//class CompleteGridCodec : ICodec {
//    // encodes/decodes every tile
//
//    override fun encodeGrid(grid: CoordinateGrid): String {
//        return grid.tiles.flatten().joinToString("") { tile ->
//            encodeBuildingId(
//                if (tile is Tile.Built) {
//                    tile.building.id
//                } else {
//                    0b1111_1111_1111
//                }
//            )
//        }
//    }
//
//    override fun decodeGrid(binaryGrid: String): CoordinateGrid {
//        val buildingIds = binaryGrid.chunked(idBitSize).map { it.toInt(2) }.chunked(CoordinateGrid.GRID_SIZE)
//        val grid = CoordinateGrid()
//
//        buildingIds.forEachIndexed { y, row ->
//            row.forEachIndexed { x, buildingId ->
//                if (buildingId != 0b1111_1111_1111) {
//                    val building = idToBuildingMap[buildingId]!!
//                    val coordinate = Coordinate(x, y)
//                    grid.tryAddBuilding(building, coordinate)
//                }
//            }
//        }
//
//        return grid
//    }
//
//}