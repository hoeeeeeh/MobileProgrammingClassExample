package com.example.finalexam2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.finalexam2022.databinding.ActivityDynamicFragmentBinding

class DynamicFragmentActivity : AppCompatActivity() {
    lateinit var binding : ActivityDynamicFragmentBinding
    val imgFragment = DynamicImageFragment()
    val itemFragment = DynamicItemFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDynamicFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }
    private fun initLayout() {
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.dynamicFrameLayout, imgFragment)
        fragment.commit()
        binding.apply{
            button.setOnClickListener{
                if(!imgFragment.isVisible){
                    val fragment = supportFragmentManager.beginTransaction()
                    fragment.addToBackStack(null)
                    fragment.replace(R.id.dynamicFrameLayout, imgFragment)
                    fragment.commit()
                }
            }
            button2.setOnClickListener {
                if(!itemFragment.isVisible){
                    val fragment = supportFragmentManager.beginTransaction()
                    fragment.addToBackStack(null)
                    fragment.replace(R.id.dynamicFrameLayout, itemFragment)
                    fragment.commit()
                }
            }
        }
    }
}