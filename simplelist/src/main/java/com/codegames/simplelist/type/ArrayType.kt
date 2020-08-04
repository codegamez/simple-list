package com.codegames.simplelist.type

import org.apache.commons.lang3.ArrayUtils

class ArrayType<T>(override var data: Array<T>) :
    ArrayInterface<Array<T>, T> {

    override fun removeAt(index: Int) {
        data = ArrayUtils.remove(data, index)
    }

    override fun get(index: Int) = data[index]

    override fun set(index: Int, value: T) = data.set(index, value)

    override fun add(value: T) {
        data += value
    }

    override fun add(index: Int, value: T) {
        @Suppress("DEPRECATION")
        data = ArrayUtils.add(data, index, value)
    }

    override fun addAll(index: Int, elements: Collection<T>) {
        var list = ArrayUtils.subarray(data, 0, index)
        list += elements
        list += ArrayUtils.subarray(data, index, data.size)
        data = list
    }

    override val size get() = data.size

}