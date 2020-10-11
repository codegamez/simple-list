package com.codegames.simplelistdemo.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codegames.simplelist.simple
import com.codegames.simplelistdemo.*
import kotlinx.android.synthetic.main.activity_all.*
import kotlinx.android.synthetic.main.footer_view.view.*
import kotlinx.android.synthetic.main.header_view.view.*
import kotlinx.android.synthetic.main.item_view_v.view.*

@SuppressLint("SetTextI18n")
class VerticalListActivity : AppCompatActivity() {

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

        // enableDivider = true
        columns = 1

        itemMargin(8)
        padding(8)
        clipToPadding = false

        headerBind(R.layout.header_view) {
            it.hv_tvTitle.text = "Demo 1"
        }

        itemHolder(R.layout.item_view_v) {

            itemView.ivv_btnAction1.setOnClickListener {
                toast("Item $adapterPosition action 1 clicked")
                adapter.removeItem(adapterPosition)
            }

            itemView.ivv_btnAction2.setOnClickListener {
                toast("Item $adapterPosition action 2 clicked")
            }

            bind { v, item, _ ->
                v.ivv_tvTitle.text = item.title
                v.ivv_tvSubtitle.text = item.subtitile
                v.ivv_tvBody.text = item.body
                v.ivv_ivImage.setBackgroundColor(item.color)
                v.ivv_ivImage.setImageResource(item.imageRes)
            }

        }

        footerBind(R.layout.footer_view) { v ->
            v.fv_btnMore.setOnClickListener {
                recyclerView.post {
                    adapter.addItemRange(fetchData(items.size))
                }
            }
        }

    }

}