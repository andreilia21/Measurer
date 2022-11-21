package com.dewerro.measurer

import com.dewerro.measurer.math.Vector2d
import org.junit.Test
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        val cords = listOf(
            Pair(Vector2d(3f, 6f), Vector2d(6f,9f))
        )

        print(calculateDistance(cords)) // 4.2426 correct
    }

    fun calculateDistance(coordinates: List<Pair<Vector2d, Vector2d>>): List<Float> {
        val distanceList = mutableListOf<Float>()

        coordinates.forEach { dotsPair ->
            val firstDot = dotsPair.first
            val secondDot = dotsPair.second

            val vector = Vector2d(secondDot.x - firstDot.x, secondDot.y - firstDot.y)

            distanceList.add(sqrt(vector.x.pow(2) + vector.y.pow(2)))
        }

        return distanceList
    }
}