package com.codegames.simplelist.type

@Suppress("unused")
interface ArrayInterface<R, T> {

    var data: R

    operator fun get(index: Int): T

    operator  fun set(index: Int, value: T)

    fun add(value: T)

    fun add(index: Int, value: T)

    fun removeAt(index: Int)

    fun addAll(index: Int, elements: Collection<T>)

    fun addAll(index: Int, elements: Array<T>) {
        addAll(index, elements.toList())
    }

    fun addAll(elements: Collection<T>) {
        addAll(lastIndex, elements)
    }

    fun addAll(elements: Array<T>) {
        addAll(lastIndex, elements)
    }

    val size: Int

    val lastIndex get() = size - 1
}