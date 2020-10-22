package com.codegames.simplelist

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codegames.swipereveallayout.SwipeRevealLayout
import com.codegames.simplelist.adapter.*
import com.codegames.simplelist.anim.SimpleItemAnimatorConfig

class SimpleTypeConf<T> {
    internal var typeId: Int = 0
    internal var isThisType: ((item: T, position: Int) -> Boolean)? = null
    internal var holder: (SimpleTypeHolder<T>.() -> Unit)? = null
    internal var swipeBind: ((view: View, item: T, position: Int) -> Unit)? = null
    internal var bind: ((view: View, item: T, position: Int) -> Unit)? = null
    internal var swipeLayout: Int? = null
    internal var layout: Int? = null
    internal var spanSize: Int = 1
    internal var enterAnimType: Int? = null
    internal var enterAnimConfig: (SimpleItemAnimatorConfig.() -> Unit)? = null
}

@Suppress("MemberVisibilityCanBePrivate", "unused", "PropertyName")
open class SimpleConf<T>(var context: Context? = null) {

    internal var typeList = mutableListOf<SimpleTypeConf<T>>()

    val SWIPE_MODE_NORMAL = SwipeRevealLayout.MODE_NORMAL
    val SWIPE_MODE_SAME_LEVEL = SwipeRevealLayout.MODE_SAME_LEVEL
    val SWIPE_DRAG_EDGE_TOP = SwipeRevealLayout.DRAG_EDGE_TOP
    val SWIPE_DRAG_EDGE_RIGHT = SwipeRevealLayout.DRAG_EDGE_RIGHT
    val SWIPE_DRAG_EDGE_BOTTOM = SwipeRevealLayout.DRAG_EDGE_BOTTOM
    val SWIPE_DRAG_EDGE_LEFT = SwipeRevealLayout.DRAG_EDGE_LEFT

    private val density get() = context?.resources?.displayMetrics?.density?.toInt() ?: 1

    internal var mAdapter: SimpleAdapter<T>? = null

    val adapter get() = mAdapter!!

    companion object {
        const val VERTICAL = RecyclerView.VERTICAL
        const val HORIZONTAL = RecyclerView.HORIZONTAL
        const val GRID = 13

        const val ITEM_ANIM_FADE_IN = 1
        const val ITEM_ANIM_SLIDE_IN = 2

        const val DIR_LEFT_TO_RIGHT = 1
        const val DIR_RIGHT_TO_LEFT = 2
        const val DIR_TOP_TO_BOTTOM = 3
        const val DIR_BOTTOM_TO_TOP = 4

    }

    var swipeDragEdge = SwipeRevealLayout.DRAG_EDGE_RIGHT
    var swipeMode = SwipeRevealLayout.MODE_NORMAL

    internal var itemHolder: (SimpleItemHolder<T>.() -> Unit)? = null
    internal var headerHolder: (SimpleHeaderHolder<T>.() -> Unit)? = null
    internal var footerHolder: (SimpleFooterHolder<T>.() -> Unit)? = null

    internal var swipeLayout: Int? = null
    internal var itemLayout: Int? = null
    internal var headerLayout: Int? = null
    internal var footerLayout: Int? = null

    internal var swipeBind = HashMap<Int, ((view: View, item: T, position: Int) -> Unit)?>()
    internal var itemBind = HashMap<Int, ((view: View, item: T, position: Int) -> Unit)?>()
    internal var headerBind: ((view: View) -> Unit)? = null
    internal var footerBind: ((view: View) -> Unit)? = null

    var getItemId: ((position: Int) -> Long)? = null

    var reverse: Boolean = false

    private var _columns = 1
    var columns
        get() = _columns
        set(value) {
            if (value <= 0)
                _columns = 0
            else {
                _columns = value
                _rows = 0
            }
        }

    private var _rows = 0
    var rows
        get() = _rows
        set(value) {
            if (value <= 0)
                _rows = 0
            else {
                _rows = value
                _columns = 0
            }
        }

    internal var itemMargin = Rect()
    internal var padding = Rect()
    internal var verticalPadding = Rect()
    internal var horizontalPadding = Rect()

    fun createType(id: Long, spanSize: Int = 1, isThisType: (item: T, position: Int) -> Boolean) {
        this.typeList.find { it.typeId == id.toInt() }
            ?: SimpleTypeConf<T>().also {
                it.typeId = id.toInt()
                it.isThisType = isThisType
                it.spanSize = spanSize
                typeList.add(it)
            }
    }

    fun typeHolder(
        typeId: Long,
        typeLayout: Int,
        swipeLayout: Int? = null,
        holder: (SimpleTypeHolder<T>.() -> Unit)?
    ) {
        val typeConf = this.typeList.find { it.typeId == typeId.toInt() }
            ?: throw Throwable("type is not exist: call createType() before typeHolder()")
        typeConf.layout = typeLayout
        typeConf.swipeLayout = swipeLayout
        typeConf.holder = holder
    }

    fun typeBind(
        typeId: Long,
        typeLayout: Int,
        bind: ((view: View, item: T, position: Int) -> Unit)?
    ) {
        val typeConf = this.typeList.find { it.typeId == typeId.toInt() }
            ?: SimpleTypeConf<T>().also {
                it.typeId = typeId.toInt()
                typeList.add(it)
            }
        typeConf.layout = typeLayout
        typeConf.bind = bind
    }

    fun padding(space: Int) {
        verticalPadding(space)
        horizontalPadding(space)
    }

    fun padding(left: Int, top: Int, right: Int, bottom: Int) {
        paddingLeft(left)
        paddingTop(top)
        paddingRight(right)
        paddingBottom(bottom)
    }

    fun paddingLeft(space: Int) {
        padding.left = space * density
    }

    fun paddingTop(space: Int) {
        padding.top = space * density
    }

    fun paddingRight(space: Int) {
        padding.right = space * density
    }

    fun paddingBottom(space: Int) {
        padding.bottom = space * density
    }

    fun verticalPadding(space: Int) {
        paddingTop(space)
        paddingBottom(space)
    }

    fun horizontalPadding(space: Int) {
        paddingLeft(space)
        paddingRight(space)
    }

    fun itemMargin(space: Int) {
        itemMargin(space, space, space, space)
    }

    fun itemMargin(left: Int, top: Int, right: Int, bottom: Int) {
        itemMargin.left = left * density
        itemMargin.top = top * density
        itemMargin.right = right * density
        itemMargin.bottom = bottom * density
    }

    fun itemHorizontalMargin(space: Int) {
        itemMargin.left = space * density
        itemMargin.right = space * density

    }

    fun itemVerticalMargin(space: Int) {
        itemMargin.top = space * density
        itemMargin.bottom = space * density
    }

    var clipToPadding: Boolean? = null
    var clipChildren: Boolean? = null
    var enableDivider: Boolean = false
    var enableLinearSnap: Boolean = false
    var enableGravitySnap: Int? = null
    var enablePagerSnap: Boolean = false

    fun itemHolder(
        itemLayout: Int,
        swipeLayout: Int? = null,
        holder: (SimpleItemHolder<T>.() -> Unit)?
    ) {
        this.itemLayout = itemLayout
        this.swipeLayout = swipeLayout
        this.itemHolder = holder
    }

    fun headerHolder(
        itemLayout: Int,
        holder: (SimpleHeaderHolder<T>.() -> Unit)?
    ) {
        this.itemLayout = itemLayout
        this.headerHolder = holder
    }

    fun footerHolder(
        itemLayout: Int,
        holder: (SimpleFooterHolder<T>.() -> Unit)?
    ) {
        this.itemLayout = itemLayout
        this.footerHolder = holder
    }

    fun itemBind(
        itemLayout: Int,
        payload: Int? = null,
        bind: ((view: View, item: T, position: Int) -> Unit)?
    ) {
        this.itemLayout = itemLayout
        this.itemBind[payload ?: SimpleAdapter.PAYLOAD_ALL] = bind
    }

    fun swipeBind(
        swipeLayout: Int,
        payload: Int? = null,
        bind: ((view: View, item: T, position: Int) -> Unit)?
    ) {
        this.swipeLayout = swipeLayout
        this.swipeBind[payload ?: SimpleAdapter.PAYLOAD_ALL] = bind
    }

    fun headerBind(
        headerLayout: Int,
        bind: ((view: View) -> Unit)?
    ) {
        this.headerLayout = headerLayout
        this.headerBind = bind
    }

    fun footerBind(
        footerLayout: Int,
        bind: ((view: View) -> Unit)?
    ) {
        this.footerLayout = footerLayout
        this.footerBind = bind
    }

}