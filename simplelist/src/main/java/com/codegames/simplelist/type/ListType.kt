package com.codegames.simplelist.type

class ListType<T>(override var data: List<T>) :
    ArrayInterface<List<T>, T> {

    override fun removeAt(index: Int) {
        data = data.filterIndexed { i, _ -> i != index }
    }

    override fun get(index: Int) = data[index]

    override fun set(index: Int, value: T) {
        data = data.mapIndexed { i, d -> if (i == index) value else d }
    }

    override fun add(value: T) {
        data += value
    }

    override fun add(index: Int, value: T) {
        data = data.toMutableList().also {
            it.add(index, value)
        }.toList()
    }

    override fun addAll(index: Int, elements: Collection<T>) {
        data = data.toMutableList().also {
            it.addAll(index, elements)
        }.toList()
    }

    override val size get() = data.size

}