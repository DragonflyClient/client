package tests

import net.inceptioncloud.minecraftmod.engine.sequence.ReversibleSequence
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import kotlin.math.roundToInt

internal class ReversibleSequenceTest
{
    @Test
    fun testSequence()
    {
        val expected = Array(201) { if (it <= 100) it else 200 - it }
        val actual = Array(201) { -1 }
        val sequence = object : ReversibleSequence<Double>(0.0, 100.0, 100)
        {
            override fun interpolate(progress: Double): Double
            {
                return (to - from) * progress + from
            }

        }

        actual[0] = 0
        for (i in 1..200)
        {
            if (i <= 100)
                sequence.next()
            else
                sequence.previous()

            actual[i] = sequence.current.roundToInt()
        }

        assertArrayEquals(expected, actual)
    }
}