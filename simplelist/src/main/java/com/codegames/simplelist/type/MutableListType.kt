package com.codegames.simplelist.type

class MutableListType<T>(override var data: MutableList<T>) :
    ArrayInterface<MutableList<T>, T> {

    override fun removeAt(index: Int) {
        data.removeAt(index)
    }

    override fun get(index: Int): T {
        return data[index]
    }

    override fun set(index: Int, value: T) {
        data[index] = value
    }

    override fun add(value: T) {
        data.add(value)
    }

    override fun add(index: Int, value: T) = data.add(index, value)

    override fun addAll(index: Int, elements: Collection<T>) {
        data.addAll(index, elements)
    }

    override val size get() = data.size

}