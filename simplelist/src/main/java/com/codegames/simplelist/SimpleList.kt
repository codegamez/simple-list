@file:Suppress("unused")

package com.codegames.simplelist

import androidx.recyclerview.widget.*
import com.codegames.simplelist.adapter.SimpleAdapter
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

// -------------

fun <T> RecyclerView.simple(data: List<T>, config: SimpleConf<T>.() -> Unit) {
    simpleListInter(data, config)
}

// --------------------------------

private fun <T> RecyclerView.simpleListInter(
    data: List<T>,
    config: SimpleConf<T>.() -> Unit
): SimpleAdapter<T> {
    val c = SimpleConf<T>(context)
    c.mAdapter = SimpleAdapter(data, c)
    config(c)

    c.typeList = c.typeList.filter { it.layout !== null }.toMutableList()

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
                return when (val type = adapter?.getItemViewType(position)) {
                    SimpleAdapter.TYPE_HEADER -> c.columns.coerceAtLeast(c.rows)
                    SimpleAdapter.TYPE_FOOTER -> c.columns.coerceAtLeast(c.rows)
                    else -> c.typeList.find { it.typeId == type }?.spanSize ?: 1
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

    return c.mAdapter!!
}