package io.github.sykq.tcc.internal

internal fun String.prependIfMissing(prependChar: Char): String {
    return if (this.startsWith(prependChar)) this else "$prependChar$this"
}