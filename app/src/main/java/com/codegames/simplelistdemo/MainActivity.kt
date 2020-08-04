package com.codegames.simplelistdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.demo.HorizontalListActivity
import com.codegames.simplelistdemo.demo.VerticalGridActivity
import com.codegames.simplelistdemo.demo.VerticalListActivity
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.item_list.view.*


class MainActivity : AppCompatActivity() {

    private val demoList = arrayOf(
        ItemModel("Demo 1", "vertical list", VerticalListActivity::class.java),
        ItemModel("Demo 2", "horizontal list", HorizontalListActivity::class.java),
        ItemModel("Demo 3", "vertical grid", VerticalGridActivity::class.java),
        ItemModel("Demo 4", "horizontal grid", VerticalListActivity::class.java),
        ItemModel("Demo 5", "view pager mode", VerticalListActivity::class.java)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all)
        bottomContainer.visibility = View.GONE
        setupList()
    }

    private fun setupList() = recyclerView.simple(demoList) {

        enableDivider = true

        itemHolder(R.layout.item_list) {

            itemView.setOnClickListener {
                Intent(this@MainActivity, item.activityClass).apply {
                    startActivity(this)
                }
            }

            bind { item, position ->
                itemView.il_tvTitle.text = item.title
                itemView.il_tvSubtitle.text = item.subtitle
            }

        }

    }

    class ItemModel(
        val title: String,
        val subtitle: String,
        val activityClass: Class<*>
    )

}