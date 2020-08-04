package com.codegames.simplelist.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codegames.simplelist.SimpleListConfig
import kotlin.math.ceil

@Suppress("MemberVisibilityCanBePrivate")
internal class SimpleSpaceItemDecoration<R, T>(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val columns: Int,
    val rows: Int,
    val isEqual: Boolean,
    val verticalOuter: Boolean,
    val horizontalOuter: Boolean,
    val config: SimpleListConfig<R, T>,
    tag: String? = null
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        handle(outRect, view, parent)
    }

    fun handle(outRect: Rect, view: View, parent: RecyclerView) {

        val position = parent.getChildAdapterPosition(view)
        val isHeader = config.headerLayout != null && position == 0
        val isFooter = config.footerLayout != null && position + 1 == parent.adapter?.itemCount
        val itemCount = (parent.adapter?.itemCount ?: 0) -
                (config.headerLayout != null).toInt() -
                (config.footerLayout != null).toInt()

        val itemPosition = position - (config.headerLayout != null).toInt()

        // scape headers
        if (isHeader || isFooter) return

        // for simple space
        if (!isEqual) {
            outRect.set(left, top, right, bottom)
            return
        }

        var fLeft: Float
        var fRight: Float
        var fTop: Float
        var fBottom: Float

        /*
        * ----------------------
        * with outer
        * 1 | 1
        * 1 | t-1 2-t | 1
        * 1 | t-1 2-t | 2t-2 3-2t | 3t-3 4-3t | 4t-4 5-4t | 5t-5 6-5t | 1
        * n-(n-1)t | nt-n
        * ----------------------
        * without outer
        * 0 | 0
        * 0 | t t | 0
        * 0 | t 1-t | 2t-1 2-2t | 3t-2 3-3t | 4t-3 4-4t | 5t-4 5-5t | 0
        * (n-1)-(n-1)t | nt-(n-1)
        * ----------------------
        */

        if (config.columns == 0 && config.rows == 0)
            throw Throwable("Wrong columns($columns) or rows($rows) value")

        val verticalItemCount =
            if (rows == 0) ceil(itemCount.toFloat() / columns).toInt() else rows
        val horizontalItemCount =
            if (columns == 0) ceil(itemCount.toFloat() / rows).toInt() else columns

        // horizontal
        if (columns != 0) {
            val space = (left + right).toFloat()
            val t = space * (horizontalItemCount + 1F) / horizontalItemCount
            val n = itemPosition % horizontalItemCount + 1
            fLeft = if (horizontalOuter)
                if (n == 1) space else n * space - (n - 1) * t
            else
                if (n == 1) 0F else (n - 1) * space - (n - 1) * t
            fRight = if (horizontalOuter)
                if (n == horizontalItemCount) space else n * t - n * space
            else
                if (n == horizontalItemCount) 0F else n * t - (n - 1) * space
        } else {
            fLeft = if (horizontalOuter && itemPosition in 0 until verticalItemCount)
                (left + right).toFloat()
            else
                (left + right) / 2F
            fRight =
                if (horizontalOuter && itemPosition in itemCount - verticalItemCount until itemCount)
                    (left + right).toFloat()
                else
                    (left + right) / 2F
        }

        // vertical
        if (rows != 0) {
//            val space = (top + bottom).toFloat()
//            val n = itemPosition % verticalItemCount + 1
//            val t = space * (verticalItemCount + 1F) / verticalItemCount
//            fTop = if (verticalOuter)
//                if (n == 1) space else (n * space - (n - 1) * t)
//            else
//                if (n == 1) 0F else ((n - 1) * space - (n - 1) * t)
//            fBottom = if (verticalOuter)
//                if (n == verticalItemCount) space else n * t - n * space
//            else
//                if (n == verticalItemCount) 0F else n * t - (n - 1) * space
            fTop = if (verticalOuter && itemPosition % verticalItemCount == 0)
                (top + bottom).toFloat()
            else
                (top + bottom) / 2F
            fBottom =
                if (verticalOuter && itemPosition % verticalItemCount == verticalItemCount - 1)
                    (top + bottom).toFloat()
                else
                    (top + bottom) / 2F
        } else {
            fTop = if (verticalOuter && itemPosition in 0 until horizontalItemCount)
                (top + bottom).toFloat()
            else
                (top + bottom) / 2F
            fBottom =
                if (verticalOuter && itemPosition in  itemCount - horizontalItemCount until horizontalItemCount)
                    (top + bottom).toFloat()
                else
                    (top + bottom) / 2F
        }

        if (config.reverse) {
            var t = fLeft
            fLeft = fRight
            fRight = t
            t = fTop
            fTop = fBottom
            fBottom = t
        }

        outRect.set(fLeft.toInt(), fTop.toInt(), fRight.toInt(), fBottom.toInt())
    }

}
