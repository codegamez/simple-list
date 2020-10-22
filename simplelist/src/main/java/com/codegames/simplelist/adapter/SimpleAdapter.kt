package com.codegames.simplelist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.codegames.simplelist.SimpleConf
import com.codegames.simplelist.SimpleTypeConf
import com.codegames.simplelist.anim.SimpleItemAnimatorConfig
import com.codegames.simplelist.anim.animateItemFadeIn
import com.codegames.simplelist.anim.animateItemSlideIn
import com.codegames.simplelist.util.MyViewHolder
import com.codegames.simplelist.util.toInt
import com.codegames.swipereveallayout.SwipeRevealLayout
import com.codegames.swipereveallayout.ViewBinderHelper
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
        const val PAYLOAD_ALL = -1000
    }

    var scrolled = false
        private set

    internal var itemEnterAnimType: Int? = null
    internal var itemEnterAnimConfig: (SimpleItemAnimatorConfig.() -> Unit)? = null

    private fun createParentSize(s: Int): Int {
        return if (s == ViewGroup.LayoutParams.MATCH_PARENT) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    fun updateItemView(rootView: ViewGroup, itemView: View) {
        val nw = createParentSize(itemView.layoutParams.width)
        val nh = createParentSize(itemView.layoutParams.height)
        if (nw != rootView.layoutParams?.width || nh != rootView.layoutParams?.height) {
            val p = MarginLayoutParams(itemView.layoutParams)
            p.setMargins(0, 0, 0, 0)
            p.width = nw
            p.height = nh
            rootView.layoutParams = p
        }
    }

    private fun createItemView(parent: ViewGroup, itemRes: Int, swipeRes: Int?): View {

        if (config.swipeLayout == null) {
            val rootView = LinearLayout(parent.context)
            rootView.clipToPadding = false
            rootView.clipChildren = false

            val itemView = LayoutInflater.from(rootView.context)
                .inflate(itemRes, rootView, false)

            rootView.addView(itemView, itemView.layoutParams)

            rootView.setPadding(
                config.itemMargin.left,
                config.itemMargin.top,
                config.itemMargin.right,
                config.itemMargin.bottom
            )

            updateItemView(rootView, itemView)

            return rootView
        }

        val rootView = SwipeRevealLayout(parent.context)
        rootView.clipToPadding = false
        rootView.clipChildren = false
        rootView.mode = config.swipeMode
        rootView.dragEdge = config.swipeDragEdge

        if (swipeRes != null) {
            val swipeView = LayoutInflater.from(rootView.context)
                .inflate(swipeRes, rootView, false)

            rootView.addView(swipeView, swipeView.layoutParams)
        }

        val itemView = LayoutInflater.from(rootView.context)
            .inflate(itemRes, rootView, false)

        rootView.addView(itemView, itemView.layoutParams)

        rootView.setupViews()

        rootView.setPadding(
            config.itemMargin.left,
            config.itemMargin.top,
            config.itemMargin.right,
            config.itemMargin.bottom
        )

        updateItemView(rootView, itemView)

        return rootView
    }

    override fun getItemId(position: Int): Long {
        val p = getItemPosition(position)
        return config.getItemId?.invoke(p) ?: super.getItemId(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {

        val customType = config.typeList.find { it.typeId == viewType }

        when (viewType) {
            customType?.typeId -> {
                val view = createItemView(parent, customType.layout!!, customType.swipeLayout)
                return SimpleTypeHolder(view, this, customType)
            }
            TYPE_ITEM -> {
                val view = createItemView(parent, config.itemLayout!!, config.swipeLayout)
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
            else -> {
                val item = getItem(position)
                config.typeList.find { it.isThisType?.invoke(item, position) == true }?.typeId
                    ?: TYPE_ITEM
            }
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

    fun handleItem(holder: SimpleItemHolder<T>, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)
        if (config.swipeLayout == null) {
            if (payloads.isEmpty()) {
                config.itemBind[PAYLOAD_ALL]?.invoke(holder.rootView, item, position)
            } else {
                payloads.mapNotNull { it as? Int }.forEach {
                    config.itemBind[it]?.invoke(holder.rootView, item, position)
                }
            }
        } else {
            val rootView = holder.rootView as SwipeRevealLayout
            viewBinderHelper.bind(
                rootView,
                ObjectUtils.identityToString(item)
            )
            if (payloads.isEmpty()) {
                config.swipeBind[PAYLOAD_ALL]?.invoke(holder.swipeView!!, item, position)
            } else {
                payloads.mapNotNull { it as? Int }.forEach {
                    config.swipeBind[it]?.invoke(holder.swipeView!!, item, position)
                }
            }
            if (payloads.isEmpty()) {
                config.itemBind[PAYLOAD_ALL]?.invoke(holder.itemView, item, position)
            } else {
                payloads.mapNotNull { it as? Int }.forEach {
                    config.itemBind[it]?.invoke(holder.itemView, item, position)
                }
            }
        }
        animateItem(holder.rootView, position, null)
    }

    fun handleType(holder: SimpleTypeHolder<T>, position: Int, config: SimpleTypeConf<T>) {
        val item = getItem(position)
        if (config.swipeLayout == null) {
            config.bind?.invoke(holder.rootView, item, position)
        } else {
            val rootView = holder.rootView as SwipeRevealLayout
            viewBinderHelper.bind(
                rootView,
                ObjectUtils.identityToString(item)
            )
            config.swipeBind?.invoke(holder.swipeView!!, item, position)
            config.bind?.invoke(holder.itemView, item, position)
        }
        animateItem(holder.rootView, position, config)
    }

    private fun animateItem(view: View, position: Int, conf: SimpleTypeConf<T>?) {
        val t = conf?.enterAnimType ?: itemEnterAnimType
        val c = conf?.enterAnimConfig ?: itemEnterAnimConfig
        if (t != null) {
            view.alpha = 0f
            view.post {
                when (t) {
                    SimpleConf.ITEM_ANIM_FADE_IN -> animateItemFadeIn(view, position, c!!)
                    SimpleConf.ITEM_ANIM_SLIDE_IN -> animateItemSlideIn(view, position, c!!)
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val viewType = getItemViewType(position)
        val customType = config.typeList.find { it.typeId == viewType }
        when (viewType) {
            customType?.typeId -> {
                @Suppress("UNCHECKED_CAST")
                handleType(holder as SimpleTypeHolder<T>, position, customType)
            }
            TYPE_ITEM -> {
                @Suppress("UNCHECKED_CAST")
                handleItem(holder as SimpleItemHolder<T>, position, payloads)
            }
            TYPE_HEADER -> config.headerBind?.invoke(holder.itemView)
            TYPE_FOOTER -> config.footerBind?.invoke(holder.itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // use onBindViewHolder with payload
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                scrolled = true
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun getItemPosition(adapterPosition: Int): Int {
        return adapterPosition - (config.headerLayout != null).toInt()
    }

    fun getAdapterPosition(itemPosition: Int): Int {
        return itemPosition + (config.headerLayout != null).toInt()
    }

    fun getItem(adapterPosition: Int): T {
        val p = getItemPosition(adapterPosition)
        return data[p]
    }

    fun removeItem(adapterPosition: Int) {
        val p = getItemPosition(adapterPosition)
        data.removeAt(p)
        scrolled = false
        notifyItemRemoved(adapterPosition)
    }

    fun removeItemRange(adapterPositionStart: Int, itemCount: Int) {
        val ps = getItemPosition(adapterPositionStart)
        val pe = ps + itemCount
        for (i in ps until pe)
            data.removeAt(i)
        scrolled = false
        notifyItemRangeRemoved(adapterPositionStart, itemCount)
    }

    fun removeAll() {
        data.clear()
        scrolled = false
        removeItemRange(getAdapterPosition(0), data.size)
    }

    fun addItemRange(adapterPositionStart: Int, items: Collection<T>) {
        val ps = getItemPosition(adapterPositionStart)
        data.addAll(ps, items)
        scrolled = false
        notifyItemRangeInserted(adapterPositionStart, items.size)
    }

    fun addItemRange(items: Collection<T>) {
        scrolled = false
        addItemRange(getAdapterPosition(data.size), items)
    }

    fun addItem(adapterPosition: Int, item: T) {
        val p = getItemPosition(adapterPosition)
        data.add(p, item)
        scrolled = false
        notifyItemInserted(adapterPosition)
    }

    fun addItem(item: T) {
        addItem(getAdapterPosition(data.lastIndex), item)
    }

    fun setItem(adapterPosition: Int, item: T) {
        val p = getItemPosition(adapterPosition)
        data[p] = item
        notifyItemChanged(adapterPosition)
    }

    fun replaceAll(items: Collection<T>) {
        removeAll()
        addItemRange(items)
    }

    fun swipeItem(adapterPosition1: Int, adapterPosition2: Int) {
        val p1 = getItemPosition(adapterPosition1)
        val p2 = getItemPosition(adapterPosition2)
        val t = getItem(p1)
        setItem(p1, getItem(p2))
        setItem(p2, t)
        notifyItemChanged(adapterPosition1)
        notifyItemChanged(adapterPosition2)
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
class SimpleTypeHolder<T>(
    itemView: View,
    private val adapter: SimpleAdapter<T>,
    private val conf: SimpleTypeConf<T>
) : MyViewHolder(itemView) {

    @Suppress("MemberVisibilityCanBePrivate")
    fun getItem(position: Int): T {
        return adapter.getItem(position)
    }

    val rootView get() = super.itemView as ViewGroup

    val itemView
        get() = _itemView

    private val _itemView
        get() = when {
            adapter.config.swipeLayout != null -> (rootView as? ViewGroup)?.getChildAt(1)!!
            else -> (rootView as? ViewGroup)?.getChildAt(0)!!
        }

    val swipeView
        get() = when {
            adapter.config.swipeLayout != null -> (rootView as? ViewGroup)?.getChildAt(0)!!
            else -> null
        }

    val item get() = getItem(adapterPosition)

    init {
        conf.holder?.invoke(this)
        adapter.updateItemView(this.rootView, this._itemView)
    }

    fun enterAnim(type: Int?, config: SimpleItemAnimatorConfig.() -> Unit) {
        val t = when (type) {
            SimpleConf.ITEM_ANIM_FADE_IN, SimpleConf.ITEM_ANIM_SLIDE_IN -> type
            else -> null
        }
        conf.enterAnimType = t
        conf.enterAnimConfig = if (t == null) null else config
    }

    fun bind(bind: ((view: View, item: T, position: Int) -> Unit)?) {
        conf.bind = bind
    }

    fun swipeBind(bind: ((view: View, item: T, position: Int) -> Unit)?) {
        conf.swipeBind = bind
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

    val rootView get() = super.itemView as ViewGroup

    val itemView
        get() = _itemView

    private val _itemView
        get() = when {
            adapter.config.swipeLayout != null -> (rootView as? ViewGroup)?.getChildAt(1)!!
            else -> (rootView as? ViewGroup)?.getChildAt(0)!!
        }

    val swipeView
        get() = when {
            adapter.config.swipeLayout != null -> (rootView as? ViewGroup)?.getChildAt(0)!!
            else -> null
        }

    val item get() = getItem(adapterPosition)

    init {
        adapter.config.itemHolder?.invoke(this)
        adapter.updateItemView(this.rootView, this._itemView)
    }

    fun bind(payload: Int? = null, bind: ((view: View, item: T, position: Int) -> Unit)?) {
        adapter.config.itemBind[payload ?: SimpleAdapter.PAYLOAD_ALL] = bind
    }


    fun enterAnim(type: Int?, config: SimpleItemAnimatorConfig.() -> Unit) {
        val t = when (type) {
            SimpleConf.ITEM_ANIM_FADE_IN, SimpleConf.ITEM_ANIM_SLIDE_IN -> type
            else -> null
        }
        adapter.itemEnterAnimType = t
        adapter.itemEnterAnimConfig = if (t == null) null else config
    }

    fun swipeBind(payload: Int? = null, bind: ((view: View, item: T, position: Int) -> Unit)?) {
        adapter.config.swipeBind[payload ?: SimpleAdapter.PAYLOAD_ALL] = bind
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