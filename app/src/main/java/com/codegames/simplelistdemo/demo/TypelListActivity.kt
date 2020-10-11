package com.codegames.simplelistdemo.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.*
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.item_view_g.view.*

@SuppressLint("SetTextI18n")
class TypeListActivity : AppCompatActivity() {

    private val items = mutableListOf<ItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
        items.addAll(fetchData(0))
        title = "Vertical List"
        btnBack.setOnClickListener { finish() }
        setupList()
    }

    private fun setupList() = recyclerView.simple(items) {

        columns = 3

        itemHolder(R.layout.item_view_g) {

            bind { v, item, _ ->
                v.ivg_tvTitle.text = item.title
                v.ivg_ivImage.setBackgroundColor(item.color)
                v.ivg_ivImage.setImageResource(item.imageRes)
            }

        }

        createType(30, 2) { _, position ->
            position % 4 == 0 || position % 4 == 3
        }

        typeHolder(30, R.layout.item_view_g) {

            bind { v, item, _ ->
                v.ivg_tvTitle.text = item.title
                v.ivg_ivImage.setBackgroundColor(item.color)
                v.ivg_ivImage.setImageResource(item.imageRes)
            }

        }

    }

}