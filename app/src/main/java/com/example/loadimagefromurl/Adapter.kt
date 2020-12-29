package com.example.loadimagefromurl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private var items: List<String>):RecyclerView.Adapter<Adapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.textView?.text = items[position]
    }

    class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){
        var textView: TextView? = null

        init {
            if (itemView != null) {
                textView = itemView.findViewById(R.id.name)
            }
        }
    }
}