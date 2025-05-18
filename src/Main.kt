import codec.core.BaseCodec
import codec.encode_ids.ValidTilesSkipEmptyIdCodec
import grid.Coordinate

// area is 44x44


// done:
// - basic encoder but not saving blocked slots at all
// - ignore blocked encoder
// - ignore spaces too small
// - place in size order 4 3 2 1 for mock to randomize
// - bool skip for empty

// todo:
// - potentially test 2 bits to store how many fields are to be skipped? 
// - check notes

// reverse approach:
// - save coords in id order
// - save tiles to next id placement
//- start with smallest, save valid tiles to next id placement (how to reduce bitcount for skip number?)


fun main() {
    //verifyCodec(SimpleIdCodec(11))
    //verifyCodec(SimpleIdCodec(9))

//    verifyCodec(ValidTilesIdCodec(11), 100)
//    verifyCodec(ValidTilesIdCodec(9), 100)

    verifyCodec(ValidTilesSkipEmptyIdCodec(11), 5)
    verifyCodec(ValidTilesSkipEmptyIdCodec(9), 5)

}

private fun verifyCodec(codec: BaseCodec, attempts: Int = 100) {
    val testResults = List(attempts) { codec.testEncoding() }
    if (testResults.all { it }) {
        println("${codec.name} validated")
    } else {
        println("${codec.name} failed")
    }
    codec.testEncoding(true)
    println()
}

//TODO: extract into file
fun calculateCoordinatesIn(range: IntRange, offset: Coordinate = Coordinate(0, 0)): List<Coordinate> =
    range.flatMap { y ->
        range.map { x -> Coordinate(x + offset.x, y + offset.y) }
    }


