package grid

sealed class Tile {
    object Empty : Tile()
    data class Built(val building: Building) : Tile()
    data class Blocked(val source: Built) : Tile()
}