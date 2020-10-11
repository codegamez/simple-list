package com.codegames.simplelistdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.demo.*
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.item_list.view.*

class MainActivity : AppCompatActivity() {

    private val demoList = listOf(
        ItemModel("Vertical List", "demo 1", VerticalListActivity::class.java),
        ItemModel("Horizontal List", "demo 2", HorizontalListActivity::class.java),
        ItemModel("Vertical Grid", "demo 3", VerticalGridActivity::class.java),
        ItemModel("Horizontal Grid", "demo 4", HorizontalGridActivity::class.java),
        ItemModel("View Pager", "demo 5", ViewPagerActivity::class.java),
        ItemModel("Swipe Menu", "demo 6", SwipeMenuActivity::class.java),
        ItemModel("Type Menu", "demo 7", TypeListActivity::class.java)
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

            bind { v, item, _ ->
                v.il_tvTitle.text = item.title
                v.il_tvSubtitle.text = item.subtitle
            }

        }

    }

    class ItemModel(
        val title: String,
        val subtitle: String,
        val activityClass: Class<*>
    )

}