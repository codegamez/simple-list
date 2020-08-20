@file:Suppress("unused")

package com.codegames.simplelist

import androidx.recyclerview.widget.*
import com.codegames.simplelist.adapter.SimpleAdapter
import com.codegames.simplelist.util.SimpleSpaceItemDecoration
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import java.lang.Integer.max

// -------------

fun <T> RecyclerView.simple(data: List<T>, config: SimpleConf<T>.() -> Unit) {
    simpleListInter(data, config)
}

// --------------------------------

private fun <T> RecyclerView.simpleListInter(
    data: List<T>,
    config: SimpleConf<T>.() -> Unit
) {
    val c = SimpleConf<T>(context)
    c.mAdapter = SimpleAdapter(data, c)
    config(c)

    // item margin
    if (c.itemMargin.left != 0
        || c.itemMargin.top != 0
        || c.itemMargin.right != 0
        || c.itemMargin.bottom != 0
    ) {
        addItemDecoration(
            SimpleSpaceItemDecoration(
                left = c.itemMargin.left,
                top = c.itemMargin.top,
                right = c.itemMargin.right,
                bottom = c.itemMargin.bottom,
                isEqual = true,
                columns = c.columns,
                rows = c.rows,
                verticalOuter = true,
                horizontalOuter = true,
                config = c
            )
        )
    }

    setPadding(
        c.padding.left,
        c.padding.top,
        c.padding.right,
        c.padding.bottom
    )

    c.clipToPadding?.let { clipToPadding = it }
    c.clipChildren?.let { clipChildren = it }

    val layoutManager: RecyclerView.LayoutManager

    // grid view
    @Suppress("CascadeIf")
    if (c.columns > 1 || c.rows > 1) {
        layoutManager = if (c.columns > 1) {
            GridLayoutManager(
                context, c.columns,
                SimpleConf.VERTICAL, c.reverse
            )
        } else {
            GridLayoutManager(
                context, c.rows,
                SimpleConf.HORIZONTAL, c.reverse
            )
        }

        // make header and footer full width
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter?.getItemViewType(position)) {
                    SimpleAdapter.TYPE_HEADER -> max(c.columns, c.rows)
                    SimpleAdapter.TYPE_FOOTER -> max(c.columns, c.rows)
                    else -> 1
                }
            }
        }
    }
    // horizontal view
    else if (c.rows == 1) {
        layoutManager = LinearLayoutManager(context, SimpleConf.HORIZONTAL, c.reverse)
        if (c.enableDivider)
            addItemDecoration(DividerItemDecoration(context, SimpleConf.HORIZONTAL))
    }
    // vertical view
    else {
        layoutManager = LinearLayoutManager(context, SimpleConf.VERTICAL, c.reverse)
        if (c.enableDivider)
            addItemDecoration(DividerItemDecoration(context, SimpleConf.VERTICAL))
    }

    when {
        c.enableLinearSnap -> {
            val helper = LinearSnapHelper()
            helper.attachToRecyclerView(this)
        }
        c.enablePagerSnap -> {
            val helper = PagerSnapHelper()
            helper.attachToRecyclerView(this)
        }
        c.enableGravitySnap != null && c.enableGravitySnap != 0 -> {
            c.enableGravitySnap?.also {
                val helper = GravitySnapHelper(it)
                helper.attachToRecyclerView(this)
            }
        }
    }

    this.layoutManager = layoutManager
    this.adapter = c.mAdapter

    c.context = null
}