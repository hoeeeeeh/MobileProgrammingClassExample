package com.example.finalexam2022

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalexam2022.databinding.ActivityMainBinding
import com.example.finalexam2022.databinding.MainAcitivityRowBinding
import org.w3c.dom.Text


/*
* 앱에 학번 이름 출력
* binding super.create 밑에
* gradle
* manifest
*
* */








class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var activityList = ArrayList<Intent>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initActivityList()
    }

    private fun initActivityList() {
        activityList.add(Intent(this, PendingIntent::class.java))
        activityList.add(Intent(this, StaticFragment::class.java))
        activityList.add(Intent(this, DynamicFragmentActivity::class.java))
        activityList.add(Intent(this, MySQLite::class.java))
        activityList.add(Intent(this, MyBroadcastReceiver::class.java))
        activityList.add(Intent(this, MyService::class.java))

        binding.recyclerView.adapter = MyAdapter(activityList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}

class MyAdapter(val items:ArrayList<Intent>) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    interface OnItemClickListener{
        //fun OnItemClick(data: MyData)
    }

    fun moveItem(oldPos: Int, newPos: Int){
        val item = items[oldPos]
        items.removeAt(oldPos)
        items.add(newPos, item)
        notifyItemMoved(oldPos, newPos)
    }

    fun removeItem(pos:Int){
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    var itemClickListener: OnItemClickListener ?= null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textView = itemView.findViewById<TextView>(R.id.textView)
        init{}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MainAcitivityRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position].toString()
        holder.textView.setOnClickListener {
            val mainActivity = it.context as MainActivity
            mainActivity.startActivity(items[position])
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}