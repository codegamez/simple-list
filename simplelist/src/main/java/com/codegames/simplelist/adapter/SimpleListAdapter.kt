package com.codegames.simplelist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codegames.simplelist.SimpleListConfig
import com.codegames.simplelist.type.ArrayInterface
import com.codegames.simplelist.util.toInt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class SimpleListAdapter<R, T>(
    private var _data: ArrayInterface<R, T>,
    val config: SimpleListConfig<R, T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data = _data.data

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_ITEM = 2
        const val TYPE_FOOTER = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_ITEM -> {
                config.itemLayout!!
                val view = LayoutInflater.from(parent.context)
                    .inflate(config.itemLayout!!, parent, false)
                return SimpleItemViewHolder(view, this)
            }
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(config.headerLayout!!, parent, false)
                return SimpleHeaderViewHolder(view, this)
            }
            TYPE_FOOTER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(config.footerLayout!!, parent, false)
                return SimpleFooterViewHolder(view, this)
            }
            else -> throw Throwable("Wrong view type ($viewType)")
        }


    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 && config.headerLayout != null -> TYPE_HEADER
            position == _data.size && config.headerLayout == null -> TYPE_FOOTER
            position == _data.size + 1 -> TYPE_FOOTER
            else -> TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return if (config.headerLayout == null && config.footerLayout == null) {
            _data.size
        } else if (config.headerLayout != null && config.footerLayout != null) {
            _data.size + 2
        } else {
            _data.size + 1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_ITEM -> config.itemBind?.invoke(holder.itemView, getItem(position), position)
            TYPE_HEADER -> config.headerBind?.invoke(holder.itemView)
            TYPE_FOOTER -> config.footerBind?.invoke(holder.itemView)
        }
    }

    private fun getItemPosition(position: Int): Int {
        return position - (config.headerLayout != null).toInt()
    }

    fun getItem(position: Int): T {
        val p = getItemPosition(position)
        return _data[p]
    }

    fun removeItem(position: Int) {
        val p = getItemPosition(position)
        _data.removeAt(p)
        notifyItemRemoved(position)
    }

    fun removeItemRange(positionStart: Int, itemCount: Int) {
        val ps = getItemPosition(positionStart)
        val pe = ps + itemCount
        for (i in ps until pe)
            _data.removeAt(i)
        notifyItemRangeRemoved(positionStart, itemCount)
    }

    fun addItemRange(positionStart: Int, items: Collection<T>) {
        val ps = getItemPosition(positionStart)
        _data.addAll(ps, items)
        notifyItemRangeInserted(positionStart, items.size)
    }

    fun addItemRange(positionStart: Int, items: Array<T>) {
        val ps = getItemPosition(positionStart)
        _data.addAll(ps, items)
        notifyItemRangeInserted(positionStart, items.size)
    }

    fun addItemRange(items: Collection<T>) {
        addItemRange(_data.size + (config.headerLayout != null).toInt(), items)
    }

    fun addItemRange(items: Array<T>) {
        addItemRange(_data.size + (config.headerLayout != null).toInt(), items)
    }

    fun addItem(position: Int, item: T) {
        val p = getItemPosition(position)
        _data.add(p, item)
        notifyItemInserted(position)
    }

    fun addItem(item: T) {
        addItem(_data.lastIndex + (config.headerLayout != null).toInt(), item)
    }

    fun setItem(position: Int, item: T) {
        val p = getItemPosition(position)
        _data[p] = item
        notifyItemChanged(position)
    }

    fun swipeItem(position1: Int, position2: Int) {
        val p1 = getItemPosition(position1)
        val p2 = getItemPosition(position2)
        val t = getItem(p1)
        setItem(p1, getItem(p2))
        setItem(p2, t)
        notifyItemChanged(position1)
        notifyItemChanged(position2)
    }

}

@Suppress("unused")
class SimpleItemViewHolder<R, T>(
    itemView: View,
    private val adapter: SimpleListAdapter<R, T>
) : RecyclerView.ViewHolder(itemView) {

    fun getItem(position: Int): T {
        return adapter.getItem(position)
    }

    val item get() = getItem(adapterPosition)

    init {
        adapter.config.itemHolder?.invoke(this)
    }

    fun bind(bind: ((item: T, position: Int) -> Unit)?) {
        adapter.config.itemBind = { _, i, p ->
            bind?.invoke(i, p)
        }
    }

}

@Suppress("unused")
class SimpleHeaderViewHolder<R, T>(
    itemView: View,
    private val adapter: SimpleListAdapter<R, T>
) : RecyclerView.ViewHolder(itemView) {

    init {

        adapter.config.headerHolder?.invoke(this)
    }

    fun bind(bind: ((view: View) -> Unit)?) {
        adapter.config.headerBind = bind
    }
}

@Suppress("unused")
class SimpleFooterViewHolder<R, T>(
    itemView: View,
    private val adapter: SimpleListAdapter<R, T>
) : RecyclerView.ViewHolder(itemView) {

    init {
        adapter.config.footerHolder?.invoke(this)
    }

    fun bind(bind: ((view: View) -> Unit)?) {
        adapter.config.footerBind = bind
    }

}