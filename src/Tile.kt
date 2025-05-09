import grid.Building

sealed class Tile {
    object Empty : Tile()
    data class Built(val building: Building) : Tile()
    data class Blocked(val source: Built) : Tile()
    // changed to data classes


//    override fun equals(other: Any?): Boolean {
//        return when (other) {
//            is Built -> this is Built && building == other.building
//            is Blocked -> this is Blocked && source.building == other.source.building
//            else -> super.equals(other)
//        }
//    }
//
//    override fun hashCode(): Int {
//        return when (this) {
//            is Built -> building.hashCode()
//            is Blocked -> source.building.hashCode()
//            else -> javaClass.hashCode()
//        }
//    }
//
//    fun compare(otherTile: Tile): Boolean {
//        return when (this) {
//            Empty -> otherTile is Empty
//            is Built -> otherTile is Built && building == otherTile.building
//            is Blocked -> otherTile is Blocked && source.building == otherTile.source.building
//        }
//    }


}