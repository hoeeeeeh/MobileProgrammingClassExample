package com.example.finalexam2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalexam2022.databinding.ActivityWebParsingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class WebParsingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWebParsingBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebParsingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}