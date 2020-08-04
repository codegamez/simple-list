package com.codegames.simplelistdemo.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.*
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.item_view_g.view.*

@SuppressLint("SetTextI18n")
class HorizontalListActivity : AppCompatActivity() {

    private val items = mutableListOf<ItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
        items.addAll(fetchData(0))
        title = "Demo"
        btnBack.setOnClickListener { finish() }
        setupList()
    }

    private fun setupList() = recyclerView.simple(items) {

        itemMargin(8)
        rows = 1

        itemHolder(R.layout.item_view_g) {

            itemView.updateLayoutParams {
                width = 170 * density
            }

            itemView.setOnClickListener {
                toast("Item $adapterPosition clicked")
            }

            bind { item, position ->
                itemView.ivg_tvTitle.text = item.title
                itemView.ivg_ivImage.setBackgroundColor(item.color)
                itemView.ivg_ivImage.setImageResource(item.imageRes)
            }

        }

    }

}