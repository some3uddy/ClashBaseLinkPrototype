//package codec.old_codecs
//
//import codec.core.ICodec
//import getIdToBuildingMap
//import grid.Building
//import grid.Coordinate
//import grid.CoordinateGrid
//import grid.Tile
//
//class IgnoreBlockedCodec : ICodec {
//    // skips blocked fields in the encoding/decoding
//
//
//    override fun encodeGrid(grid: CoordinateGrid): String {
//        return grid.tileList.filter {
//            it !is Tile.Blocked
//        }.joinToString("") { tile ->
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
//        val idToBuildingMap: Map<Int, Building> = getIdToBuildingMap()
//
//        val buildingIds = binaryGrid
//            .chunked(idBitSize)
//            .map {
//                val id = it.toInt(2)
//                idToBuildingMap[id]
//            }
//            .iterator()
//
//        val grid = CoordinateGrid()
//        var currentBuilding = buildingIds.next()
//
//        outer@ for (y in CoordinateGrid.COORDINATE_RANGE) {
//            for (x in CoordinateGrid.COORDINATE_RANGE) {
//                val coordinate = Coordinate(x, y)
//                if (grid.isBlocked(coordinate)) {
//                    continue
//                }
//
//                if (currentBuilding != null) {
//                    if (!grid.tryAddBuilding(currentBuilding, coordinate)) {
//                        continue
//                    }
//                }
//
//                if (!buildingIds.hasNext()) {
//                    break@outer
//                }
//
//                currentBuilding = buildingIds.next()
//            }
//        }
//        return grid
//    }
//
//}