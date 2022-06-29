package com.example.finalexam2022

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.example.finalexam2022.databinding.ActivityPendingIntentBinding
import com.example.finalexam2022.databinding.PendingIntentTimepickerdlgBinding
import java.util.*


/*

Pending Intent => 인텐트는 인텐트인데, 바로 사용할 인텐트가 아니라 해당 인텐트를 들고 있다가 나중에 사용을 하게끔
Broadcast, service, activity , activities 에 사용을 넘길 수 있다.

*/

class PendingIntent : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private lateinit var binding : ActivityPendingIntentBinding
    private var message = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPendingIntentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initLayout2() // calendarView + dlg 만들어서 사용
        initLayout() //  TimePickerDlg  + onTimeSet 사용
    }

    private fun initLayout2() {
        binding.calendarView.setOnDateChangeListener{ calendarView, i, i2, i3 ->
            val dlgBinding = PendingIntentTimepickerdlgBinding.inflate(layoutInflater)
            val dlgBuilder = AlertDialog.Builder(this)
            dlgBuilder.setView(dlgBinding.root)
                .setPositiveButton("추가"){
                        _,_ ->
                    message =
                        dlgBinding.timePicker.hour.toString() + "시" +
                                dlgBinding.timePicker.minute.toString() + "분" +
                                dlgBinding.message.text.toString()
                    val timerTask = object: TimerTask(){
                        override fun run() {
                            makeNotification()
                        }
                    }
                    val timer = Timer()
                    timer.schedule(timerTask, 2000)
                    Toast.makeText(this, "알림이 추가됨", Toast.LENGTH_SHORT).show()

                }
                .setNegativeButton("취소"){
                        _,_ ->

                }
                .show()
        }
    }

    private fun initLayout() {
        binding.calendarView.setOnDateChangeListener { calendarView, i, i2, i3 ->
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, this,hour,minute, true)
            timePicker.show()
        }
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        if(p0 != null){
            message = p1.toString() + "시" + p2.toString() + "분"
            val timerTask = object: TimerTask(){
                override fun run() {
                    makeNotification()
                }
            }
            val timer = Timer()
            timer.schedule(timerTask, 2000)
            Toast.makeText(this, "알림이 추가됨", Toast.LENGTH_SHORT).show()
        }
    }

    fun makeNotification(){
        /*
        채널 만들고,
        NotificationCompat.builder 로 빌드하고,
        intent 만들어서 pendingIntent 에 넣고
        Notification Manager 로 createNotificationChannel
        Notification Manager 로 Notify
         */
        val id = "MyChannel"
        val name = "TimeCheckChannel"
        val notificationChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
        // A representation of settings that apply to a collection of similarly themed notifications.

        notificationChannel.enableVibration(true)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE // 락스크린에 출력하진 않음.(PRIVATE)

        val builder = NotificationCompat.Builder(this, id) // id == notificationChannel id
            .setSmallIcon(R.drawable.ic_baseline_alarm_24)
            .setContentTitle("일정 알람")
            .setContentText(message)
            .setAutoCancel(true)

        // notification 이 가지는 pendingIntent 의 Intent
        val intent = Intent(this, com.example.finalexam2022.PendingIntent::class.java)
        intent.putExtra("time", message)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        // 앱이 실행중이 아니면 새로운 태스크로 만들어서 실행 , 실행 중이면 얘를 제일 상위로 올려서 실행.

        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // PendingIntent.FLAG_UPDATE_CURRENT => 알람이 여러개 울리면 현재 것 업데이트.

        builder.setContentIntent(pendingIntent)


        val notification = builder.build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
        manager.notify(10, notification)
    }


}