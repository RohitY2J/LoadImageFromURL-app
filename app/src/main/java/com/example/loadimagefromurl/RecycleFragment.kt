package com.example.loadimagefromurl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class RecycleFragment : Fragment() {
    private lateinit var recycleView: RecyclerView
    private lateinit var items: List<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleView = view.findViewById(R.id.recycleView)
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = Adapter(getList())
    }

    fun getList(): List<String>{
        items = listOf("Sharad","Shanta","Hatim","Harke")
        return items
    }
}