package net.inceptioncloud.dragonfly.versioning

import net.inceptioncloud.dragonfly.versioning.DragonflyVersion.compareVersions
import org.junit.Assert.*
import org.junit.Test

class DragonflyVersionTest {

    @Test
    fun testEquality() = assertEquals(
        "two version objects constructed with the same parts should be equal",
        Version(1, 3, 14, 2),
        Version(1, 3, 14, 2)
    )

    @Test
    fun testOfString() = assertEquals(
        "constructing a version of a string should lead to the same result as the constructor invocation",
        Version.of("1.3.14.2"),
        Version(1, 3, 14, 2)
    )

    @Test
    fun testOfStringError() = assertNull(
        "constructing a version from an invalid string should result in null",
        Version.of("Hello.5.12")
    )

    @Test
    fun testCompareIdentical() = assertEquals(
        "versions should be identical",
        0,
        compareVersions(Version(1, 3, 2, 9), Version(1, 3, 2, 9))
    )

    @Test
    fun testCompareNewer() = assertEquals(
        "the first version should be newer than the second one",
        1,
        compareVersions(Version(1, 4, 0, 2), Version(1, 3, 2, 14))
    )

    @Test
    fun testCompareOlder() = assertEquals(
        "the first version should be older than the second one",
        -1,
        compareVersions(Version(2, 7, 9, 0), Version(3, 2, 12, 8))
    )
}