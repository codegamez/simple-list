@file:Suppress("unused")

package com.codegames.simplelist

import androidx.recyclerview.widget.*
import com.codegames.simplelist.adapter.SimpleListAdapter
import com.codegames.simplelist.type.*
import com.codegames.simplelist.util.SimpleSpaceItemDecoration
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import java.lang.Integer.max

typealias ConfigClosure<R, T> = SimpleListConfig<R, T>.() -> Unit

// -------------

fun <T> RecyclerView.simple(data: List<T>, config: ConfigClosure<List<T>, T>) {
    simpleListInter(data, config)
}

fun <T> RecyclerView.simple(data: Array<T>, config: ConfigClosure<Array<T>, T>) {
    simpleListInter(data, config)
}

fun <T> RecyclerView.simple(data: ArrayList<T>, config: ConfigClosure<ArrayList<T>, T>) {
    simpleListInter(data, config)
}

// --------------------

private fun <T> RecyclerView.simpleListInter(
    data: Array<T>,
    config: ConfigClosure<Array<T>, T>
) {
    val a = ArrayType(data)
    simpleListInter(a, config)
}

private fun <T> RecyclerView.simpleListInter(
    data: List<T>,
    config: ConfigClosure<List<T>, T>
) {
    @Suppress("UNCHECKED_CAST")
    if (data is MutableList) {
        config as ConfigClosure<MutableList<T>, T>
        simpleListInter(MutableListType(data), config)
    } else
        simpleListInter(ListType(data), config)
}

private fun <T> RecyclerView.simpleListInter(
    data: ArrayList<T>,
    config: ConfigClosure<ArrayList<T>, T>
) {
    val a = ArrayListType(data)
    simpleListInter(a, config)
}

// --------------------------------

private fun <R, T> RecyclerView.simpleListInter(
    data: ArrayInterface<R, T>,
    config: SimpleListConfig<R, T>.() -> Unit
) {
    val c = SimpleListConfig<R, T>(context)
    c.mAdapter = SimpleListAdapter(data, c)
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
                verticalOuter = c.verticalItemOuterMargin,
                horizontalOuter = c.horizontalItemOuterMargin,
                config = c
            )
        )
    }

    val layoutManager: RecyclerView.LayoutManager

    // grid view
    @Suppress("CascadeIf")
    if (c.columns > 1 || c.rows > 1) {
        layoutManager = if (c.columns > 1) {
            GridLayoutManager(
                context, c.columns,
                SimpleListConfig.VERTICAL, c.reverse
            )
        } else {
            GridLayoutManager(
                context, c.rows,
                SimpleListConfig.HORIZONTAL, c.reverse
            )
        }

        // make header and footer full width
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter?.getItemViewType(position)) {
                    SimpleListAdapter.TYPE_HEADER -> max(c.columns, c.rows)
                    SimpleListAdapter.TYPE_FOOTER -> max(c.columns, c.rows)
                    else -> 1
                }
            }
        }
    }
    // horizontal view
    else if (c.rows == 1) {
        layoutManager = LinearLayoutManager(context, SimpleListConfig.HORIZONTAL, c.reverse)
        if (c.enableDivider)
            addItemDecoration(DividerItemDecoration(context, SimpleListConfig.HORIZONTAL))
    }
    // vertical view
    else {
        layoutManager = LinearLayoutManager(context, SimpleListConfig.VERTICAL, c.reverse)
        if (c.enableDivider)
            addItemDecoration(DividerItemDecoration(context, SimpleListConfig.VERTICAL))
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