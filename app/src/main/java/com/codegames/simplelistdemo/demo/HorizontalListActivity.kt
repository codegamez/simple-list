package com.codegames.simplelistdemo.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.codegames.simplelist.SimpleConf
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.*
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.header_view.view.*
import kotlinx.android.synthetic.main.item_view_g.view.*

@SuppressLint("SetTextI18n")
class HorizontalListActivity : AppCompatActivity() {

    private val items = mutableListOf<ItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
        recyclerView.updateLayoutParams {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        items.addAll(fetchData(0))
        title = "Horizontal List"
        btnBack.setOnClickListener { finish() }
        setupList()
    }

    private fun setupList() = recyclerView.simple(items) {

        rows = 1

        itemMargin(8)
        padding(8)
        clipToPadding = false

        recyclerView.setHasFixedSize(true)

        itemHolder(R.layout.item_view_g) {

            enterAnim(SimpleConf.ITEM_ANIM_SLIDE_IN) {
                duration = 300
                direction = SimpleConf.DIR_TOP_TO_BOTTOM
            }

            itemView.updateLayoutParams {
                width = 170 * density
            }

            itemView.setOnClickListener {
                toast("Item $adapterPosition clicked")
            }

            bind { v, item, _ ->
                v.ivg_tvTitle.text = item.title
                v.ivg_ivImage.setBackgroundColor(item.color)
                v.ivg_ivImage.setImageResource(item.imageRes)
            }

        }

    }

}