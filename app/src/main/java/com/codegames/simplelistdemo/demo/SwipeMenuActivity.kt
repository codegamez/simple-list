package com.codegames.simplelistdemo.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.ItemModel
import com.codegames.simplelistdemo.R
import com.codegames.simplelistdemo.fetchData
import com.codegames.simplelistdemo.toast
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.item_view_h.view.*
import kotlinx.android.synthetic.main.menu_view_h.view.*

@SuppressLint("SetTextI18n")
class SwipeMenuActivity : AppCompatActivity() {

    private val items = mutableListOf<ItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
        items.addAll(fetchData(0))
        title = "Swipe Menu"
        btnBack.setOnClickListener { finish() }
        setupList()
    }

    private fun setupList() = recyclerView.simple(items) {

        columns = 1

        itemMargin(8)
        padding(8)
        clipToPadding = false
        clipChildren = false

        itemHolder(R.layout.item_view_h, R.layout.menu_view_h) {

            bind { v, item, _ ->
                v.ivh_tvTitle.text = item.title
                v.ivh_tvSubtitle.text = item.subtitile
                v.ivh_ivImage.setBackgroundColor(item.color)
                v.ivh_ivImage.setImageResource(item.imageRes)
            }

            swipeView?.also { v ->
                v.mvh_btnDelete.setOnClickListener {
                    v.post {
                        adapter.removeItem(adapterPosition)
                    }
                }
                v.mvh_btnMessage.setOnClickListener {
                    toast(item.title)
                }
            }

        }

    }

}