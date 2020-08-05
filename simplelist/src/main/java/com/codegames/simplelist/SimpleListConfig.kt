package com.codegames.simplelist

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.codegames.simplelist.adapter.SimpleFooterViewHolder
import com.codegames.simplelist.adapter.SimpleHeaderViewHolder
import com.codegames.simplelist.adapter.SimpleItemViewHolder
import com.codegames.simplelist.adapter.SimpleListAdapter

@Suppress("MemberVisibilityCanBePrivate", "unused")
class SimpleListConfig<R, T>(var context: Context? = null) {

    private val density get() = context?.resources?.displayMetrics?.density?.toInt() ?: 1

    internal var mAdapter: SimpleListAdapter<R, T>? = null

    val adapter get() = mAdapter!!

    companion object {
        const val VERTICAL = RecyclerView.VERTICAL
        const val HORIZONTAL = RecyclerView.HORIZONTAL
        const val GRID = 13
    }

    var swipeDragEdge = SwipeRevealLayout.DRAG_EDGE_RIGHT
    var swipeMode = SwipeRevealLayout.MODE_NORMAL

    internal var itemHolder: (SimpleItemViewHolder<R, T>.() -> Unit)? = null
    internal var headerHolder: (SimpleHeaderViewHolder<R, T>.() -> Unit)? = null
    internal var footerHolder: (SimpleFooterViewHolder<R, T>.() -> Unit)? = null

    internal var swipeLayout: Int? = null
    internal var itemLayout: Int? = null
    internal var headerLayout: Int? = null
    internal var footerLayout: Int? = null

    internal var swipeBind: ((view: View, item: T, position: Int) -> Unit)? = null
    internal var itemBind: ((view: View, item: T, position: Int) -> Unit)? = null
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

    fun padding(space: Int) {
        verticalPadding(space)
        horizontalPadding(space)
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
        itemVerticalMargin(space)
        itemHorizontalMargin(space)
    }

    fun itemHorizontalMargin(space: Int) {
        itemMargin.left = space * density / 2
        itemMargin.right = space * density / 2

    }

    fun itemVerticalMargin(space: Int) {
        itemMargin.top = space * density / 2
        itemMargin.bottom = space * density / 2
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
        holder: (SimpleItemViewHolder<R, T>.() -> Unit)?
    ) {
        this.itemLayout = itemLayout
        this.swipeLayout = swipeLayout
        this.itemHolder = holder
    }

    fun headerHolder(
        itemLayout: Int,
        holder: (SimpleHeaderViewHolder<R, T>.() -> Unit)?
    ) {
        this.itemLayout = itemLayout
        this.headerHolder = holder
    }

    fun footerHolder(
        itemLayout: Int,
        holder: (SimpleFooterViewHolder<R, T>.() -> Unit)?
    ) {
        this.itemLayout = itemLayout
        this.footerHolder = holder
    }

    fun itemBind(
        itemLayout: Int,
        bind: ((view: View, item: T, position: Int) -> Unit)?
    ) {
        this.itemLayout = itemLayout
        this.itemBind = bind
    }

    fun swipeBind(
        swipeLayout: Int,
        bind: ((view: View, item: T, position: Int) -> Unit)?
    ) {
        this.swipeLayout = swipeLayout
        this.swipeBind = bind
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