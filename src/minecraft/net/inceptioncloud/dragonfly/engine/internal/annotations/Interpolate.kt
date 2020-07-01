package net.inceptioncloud.dragonfly.engine.internal.annotations

/**
 * Marks all member properties of a widget that can be interpolated.
 * These are required for animations like the morph transition.
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Interpolate 