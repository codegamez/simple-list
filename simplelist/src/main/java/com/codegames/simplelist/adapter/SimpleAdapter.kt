package com.codegames.simplelist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.codegames.simplelist.SimpleConf
import com.codegames.simplelist.util.MyViewHolder
import com.codegames.simplelist.util.toInt
import kotlinx.android.synthetic.main.swipe_revieal_layout_same_level.view.*
import org.apache.commons.lang3.ObjectUtils


@Suppress("MemberVisibilityCanBePrivate", "unused")
class SimpleAdapter<T>(
    var data: List<T>,
    val config: SimpleConf<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }

    companion object {
        const val TYPE_HEADER = -1000
        const val TYPE_ITEM = -1001
        const val TYPE_FOOTER = -1003
    }

    private fun createItemView(parent: ViewGroup, itemRes: Int, swipeRes: Int?): View {
        val container = LayoutInflater.from(parent.context)
            .inflate(
                if(config.swipeMode == SwipeRevealLayout.MODE_SAME_LEVEL)
                    com.codegames.simplelist.R.layout.swipe_revieal_layout_same_level
                else
                    com.codegames.simplelist.R.layout.swipe_revieal_layout_normal,
                parent,
                false
            ) as SwipeRevealLayout
        container.dragEdge = config.swipeDragEdge
        val itemContainer = container.swipe_reveal_layout_main
        val itemView = LayoutInflater.from(itemContainer.context)
            .inflate(itemRes, itemContainer, false)

        var lp = itemView.layoutParams
        if (lp is MarginLayoutParams) {
            lp.setMargins(0, 0, 0, 0)
        }
        itemContainer.layoutParams = lp
        container.layoutParams = lp

        itemContainer.addView(itemView, itemView.layoutParams)

        if (swipeRes != null) {
            val secondaryContainer = container.swipe_reveal_layout_secondary
            val secondaryView = LayoutInflater.from(secondaryContainer.context)
                .inflate(swipeRes, secondaryContainer, false)

            lp = secondaryView.layoutParams
            if (lp is MarginLayoutParams) {
                lp.setMargins(0, 0, 0, 0)
            }
            secondaryContainer.layoutParams = lp

            secondaryContainer.addView(secondaryView)
        }

        return container
    }

    override fun getItemId(position: Int): Long {
        val p = getItemPosition(position)
        return config.getItemId?.invoke(p) ?: super.getItemId(p)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_ITEM -> {
                val view = if (config.swipeLayout == null) {
                    LayoutInflater.from(parent.context)
                        .inflate(config.itemLayout!!, parent, false)
                } else {
                    createItemView(parent, config.itemLayout!!, config.swipeLayout)
                }
                return SimpleItemHolder(view, this)
            }
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(config.headerLayout!!, parent, false)
                return SimpleHeaderHolder(view, this)
            }
            TYPE_FOOTER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(config.footerLayout!!, parent, false)
                return SimpleFooterHolder(view, this)
            }
            else -> throw Throwable("Wrong view type ($viewType)")
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 && config.headerLayout != null -> TYPE_HEADER
            position == data.size && config.headerLayout == null -> TYPE_FOOTER
            position == data.size + 1 -> TYPE_FOOTER
            else -> TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return if (config.headerLayout == null && config.footerLayout == null) {
            data.size
        } else if (config.headerLayout != null && config.footerLayout != null) {
            data.size + 2
        } else {
            data.size + 1
        }
    }

    fun handleItem(holder: SimpleItemHolder<T>, position: Int) {
        val item = getItem(position)
        if (config.swipeLayout == null) {
            config.itemBind?.invoke(holder.rootView, item, position)
        } else {
            val rootView = holder.rootView as SwipeRevealLayout
            viewBinderHelper.bind(
                rootView,
                ObjectUtils.identityToString(item)
            )
            config.swipeBind?.invoke(holder.swipeView!!, item, position)
            config.itemBind?.invoke(holder.itemView, item, position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_ITEM -> {
                @Suppress("UNCHECKED_CAST")
                handleItem(holder as SimpleItemHolder<T>, position)
            }
            TYPE_HEADER -> config.headerBind?.invoke(holder.itemView)
            TYPE_FOOTER -> config.footerBind?.invoke(holder.itemView)
        }
    }

    fun getItemPosition(position: Int): Int {
        return position - (config.headerLayout != null).toInt()
    }

    fun getAdapterPosition(position: Int): Int {
        return position + (config.headerLayout != null).toInt()
    }

    fun getItem(position: Int): T {
        val p = getItemPosition(position)
        return data[p]
    }

    fun removeItem(position: Int) {
        val p = getItemPosition(position)
        data.removeAt(p)
        notifyItemRemoved(position)
    }

    fun removeItemRange(positionStart: Int, itemCount: Int) {
        val ps = getItemPosition(positionStart)
        val pe = ps + itemCount
        for (i in ps until pe)
            data.removeAt(i)
        notifyItemRangeRemoved(positionStart, itemCount)
    }

    fun removeAll() {
        data.clear()
        removeItemRange(getAdapterPosition(0), data.size)
    }

    fun addItemRange(positionStart: Int, items: Collection<T>) {
        val ps = getItemPosition(positionStart)
        data.addAll(ps, items)
        notifyItemRangeInserted(positionStart, items.size)
    }

    fun addItemRange(items: Collection<T>) {
        addItemRange(getAdapterPosition(data.size), items)
    }

    fun addItem(position: Int, item: T) {
        val p = getItemPosition(position)
        data.add(p, item)
        notifyItemInserted(position)
    }

    fun addItem(item: T) {
        addItem(getAdapterPosition(data.lastIndex), item)
    }

    fun setItem(position: Int, item: T) {
        val p = getItemPosition(position)
        data[p] = item
        notifyItemChanged(position)
    }

    fun replaceAll(items: Collection<T>) {
        removeAll()
        addItemRange(items)
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

private operator fun <T> List<T>.set(index: Int, value: T) {
    when (this) {
        is ArrayList -> {
            this[index] = value
        }
        is MutableList -> {
            this[index] = value
        }
        else -> {
            throw Throwable("List is not mutable")
        }
    }
}

private fun <T> List<T>.add(value: T) {
    when (this) {
        is ArrayList -> {
            this.add(value)
        }
        is MutableList -> {
            this.add(value)
        }
        else -> {
            throw Throwable("List is not mutable")
        }
    }
}

private fun <T> List<T>.add(index: Int, value: T) {
    when (this) {
        is ArrayList -> {
            this.add(index, value)
        }
        is MutableList -> {
            this.add(index, value)
        }
        else -> {
            throw Throwable("List is not mutable")
        }
    }
}

private fun <T> List<T>.addAll(index: Int, elements: Collection<T>) {
    when (this) {
        is ArrayList -> {
            this.addAll(index, elements)
        }
        is MutableList -> {
            this.addAll(index, elements)
        }
        else -> {
            throw Throwable("List is not mutable")
        }
    }
}

private fun <T> List<T>.removeAt(index: Int) {
    when (this) {
        is ArrayList -> {
            this.removeAt(index)
        }
        is MutableList -> {
            this.removeAt(index)
        }
        else -> {
            throw Throwable("List is not mutable")
        }
    }
}

private fun <T> List<T>.clear() {
    when (this) {
        is ArrayList -> {
            this.clear()
        }
        is MutableList -> {
            this.clear()
        }
        else -> {
            throw Throwable("List is not mutable")
        }
    }
}

@Suppress("unused")
class SimpleItemHolder<T>(
    itemView: View,
    private val adapter: SimpleAdapter<T>
) : MyViewHolder(itemView) {

    @Suppress("MemberVisibilityCanBePrivate")
    fun getItem(position: Int): T {
        return adapter.getItem(position)
    }

    val rootView get() = super.itemView

    val itemView
        get() = (rootView as? ViewGroup)
            ?.findViewById<ViewGroup>(com.codegames.simplelist.R.id.swipe_reveal_layout_main)
            ?.getChildAt(0) ?: rootView

    val swipeView: View?
        get() = (rootView as? ViewGroup)
            ?.findViewById<ViewGroup>(com.codegames.simplelist.R.id.swipe_reveal_layout_secondary)
            ?.getChildAt(0)

    val item get() = getItem(adapterPosition)

    init {
        adapter.config.itemHolder?.invoke(this)
    }

    fun bind(bind: ((view: View, item: T, position: Int) -> Unit)?) {
        adapter.config.itemBind = bind
    }

    fun swipeBind(bind: ((view: View, item: T, position: Int) -> Unit)?) {
        adapter.config.swipeBind = bind
    }

}

@Suppress("unused")
class SimpleHeaderHolder<T>(
    itemView: View,
    private val adapter: SimpleAdapter<T>
) : RecyclerView.ViewHolder(itemView) {

    init {
        adapter.config.headerHolder?.invoke(this)
    }

    fun bind(bind: ((view: View) -> Unit)?) {
        adapter.config.headerBind = bind
    }
}

@Suppress("unused")
class SimpleFooterHolder<T>(
    itemView: View,
    private val adapter: SimpleAdapter<T>
) : RecyclerView.ViewHolder(itemView) {

    init {
        adapter.config.footerHolder?.invoke(this)
    }

    fun bind(bind: ((view: View) -> Unit)?) {
        adapter.config.footerBind = bind
    }

}