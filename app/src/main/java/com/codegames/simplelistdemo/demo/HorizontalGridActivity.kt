package com.codegames.simplelistdemo.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.*
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.item_view_g.view.*

@SuppressLint("SetTextI18n")
class HorizontalGridActivity : AppCompatActivity() {

    private val items = mutableListOf<ItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
        recyclerView.updateLayoutParams {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        items.addAll(fetchData(0))
        title = "Horizontal Grid"
        btnBack.setOnClickListener { finish() }
        setupList()
    }

    private fun setupList() = recyclerView.simple(items) {

        itemMargin(16)
        rows = 2

        itemHolder(R.layout.item_view_g) {

            itemMargin(1)
            itemHorizontalMargin(2)
            itemVerticalMargin(2)

            itemView.updateLayoutParams {
                width = 170 * density
            }

            itemView.setOnClickListener {
                toast("Item $adapterPosition clicked")
            }

            bind { v, item, _ ->
                v.ivg_tvTitle.text = item.title
                v.ivg_ivImage.setImageResource(item.imageRes)
                v.ivg_ivImage.setBackgroundColor(item.color)
            }

        }

    }

}