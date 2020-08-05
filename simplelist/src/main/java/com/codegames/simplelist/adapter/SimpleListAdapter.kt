package com.codegames.simplelist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.codegames.simplelist.SimpleListConfig
import com.codegames.simplelist.type.ArrayInterface
import com.codegames.simplelist.util.MyViewHolder
import com.codegames.simplelist.util.toInt
import kotlinx.android.synthetic.main.swipe_revieal_layout_same_level.view.*
import org.apache.commons.lang3.ObjectUtils


@Suppress("MemberVisibilityCanBePrivate", "unused")
class SimpleListAdapter<R, T>(
    private var _data: ArrayInterface<R, T>,
    val config: SimpleListConfig<R, T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }

    val data = _data.data

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

    fun handleItem(holder: SimpleItemViewHolder<R, T>, position: Int) {
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
                handleItem(holder as SimpleItemViewHolder<R, T>, position)
            }
            TYPE_HEADER -> config.headerBind?.invoke(holder.itemView)
            TYPE_FOOTER -> config.footerBind?.invoke(holder.itemView)
        }
    }

    fun getItemPosition(position: Int): Int {
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