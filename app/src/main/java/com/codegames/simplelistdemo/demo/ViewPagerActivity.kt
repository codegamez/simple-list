package com.codegames.simplelistdemo.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.*
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.footer_view.view.*
import kotlinx.android.synthetic.main.header_view.view.*
import kotlinx.android.synthetic.main.item_view_p.view.*
import kotlinx.android.synthetic.main.item_view_v.view.*

@SuppressLint("SetTextI18n")
class ViewPagerActivity : AppCompatActivity() {

    private val items = mutableListOf<ItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
        items.addAll(fetchData(0))
        title = "View Pager"
        btnBack.setOnClickListener { finish() }
        setupList()
    }

    private fun setupList() = recyclerView.simple(items) {

        rows = 1
        enablePagerSnap = true

        itemBind(R.layout.item_view_p) { v, item, _ ->
            v.ivp_tvTitle.text = item.title
            v.ivp_rootView.setBackgroundColor(item.color)
        }

    }

}