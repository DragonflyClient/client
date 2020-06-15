package net.inceptioncloud.minecraftmod.engine.internal.annotations

/**
 * Marks all member properties of a widget represent the state of the widget.
 *
 * They are used to compare the state of two widgets while member properties without
 * this annotation are seen as utility and skipped during the process.
 *
 * This annotation is only required for properties that are not annotated with [Interpolate].
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class State 