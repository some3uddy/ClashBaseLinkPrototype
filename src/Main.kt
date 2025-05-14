import codec.core.BaseCodec
import codec.encode_ids.SimpleIdCodec
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

    verifyCodec(SimpleIdCodec(11), 5)
    verifyCodec(SimpleIdCodec(9), 5)

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

fun runAll() {
//    println("complete")
//    runExperiment(CompleteGridCodec())
//    println()
//    println("ignore blocked")
//    runExperiment(IgnoreBlockedCodec())
//    println()
//    println("ignore blocked and too small")
//    runExperiment(IgnoreBlockedAndTooSmallCodec())
//    println()
//    println("skip invalid with bool")
//    runExperiment(SkipInvalidWithBoolCodec()) 
//    println()
    // println("coordinates per id codec")
    // runExperiment(CoordinatesPerIdCodec())
}

fun calculateCoordinatesIn(range: IntRange, offset: Coordinate = Coordinate(0, 0)): List<Coordinate> =
    range.flatMap { y ->
        range.map { x -> Coordinate(x + offset.x, y + offset.y) }
    }


