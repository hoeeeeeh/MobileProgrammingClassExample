package com.example.finalexam2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalexam2022.databinding.ActivityStaticFragmentBinding

class StaticFragment : AppCompatActivity() {
    private lateinit var binding : ActivityStaticFragmentBinding
    val myViewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaticFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }
}

class MyViewModel : ViewModel() {
    val selectednum = MutableLiveData<Int>(0)
    fun setLiveData(num:Int){
        selectednum.value = num
    }
}