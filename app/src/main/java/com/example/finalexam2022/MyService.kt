package com.example.finalexam2022

import android.app.Service
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.finalexam2022.databinding.ActivityMainBinding
import com.example.finalexam2022.databinding.ActivityMyServiceBinding

class MyService : AppCompatActivity() {
    var thread : Thread? = null
    var num = 0 // 몇 번째 쓰레드?


    lateinit var binding : ActivityMyServiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.apply{
            btnStartThread.setOnClickListener {
                if(thread == null){
                    thread = object: Thread("MyThread"){
                        override fun run() {
                            // super.run()
                            try{
                                for(i in 0..10){
                                    Log.i("MyThread $num", "Count : $i")
                                    Thread.sleep(1000)
                                }
                                thread = null // 쓰레드 다시 생성하기 위해서
                            }catch(e:InterruptedException){
                                Thread.currentThread().interrupt()
                            }
                        }
                    }
                }
                thread!!.start()
                num++
            }
            btnStopThread.setOnClickListener {
                if(thread != null){
                    thread!!.interrupt() // 쓰레드를 강제종료. 슬립할 때 인터럽트 걸리면 예외사항이 생긴다. (InterruptedException)
                }
                thread = null

            }
            btnStartService.setOnClickListener {
                val intent = Intent(this@MyService, MyServiceclass::class.java)
                startService(intent)

            }
            btnStopService.setOnClickListener {
                val intent = Intent(this@MyService, MyServiceclass::class.java)
                stopService(intent)

            }
        }
    }
}

class MyServiceclass : Service() {
    var thread : Thread? = null
    var num = 0 // 몇 번째 쓰레드?


    override fun onCreate() {
        super.onCreate()
        Log.i("MyService", "onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("MyService", "onStartCommand()")
        if(thread == null){
            thread = object: Thread("MyThread"){
                override fun run() {
                    // super.run()
                    try{
                        for(i in 0..10){
                            Log.i("MyThread $num", "Count : $i")
                            Thread.sleep(1000)
                        }
                        thread = null // 쓰레드 다시 생성하기 위해서
                    }catch(e:InterruptedException){
                        Thread.currentThread().interrupt()
                    }
                }
            }
            thread!!.start()
            num++
        }

        return START_STICKY
        // 시스템에 의해서 서비스가 강제로 종료 당할 수 있기 때문에 , 이런 경우 대처할 커맨드를 넣어준다 . [START_STICKY : 자동으로 재 실행]
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MyService", "onDestroy()")
        if(thread != null){
            thread!!.interrupt() // 쓰레드를 강제종료. 슬립할 때 인터럽트 걸리면 예외사항이 생긴다. (InterruptedException)
        }
        thread = null
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}