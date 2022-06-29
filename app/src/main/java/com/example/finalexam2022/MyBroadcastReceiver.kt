package com.example.finalexam2022

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.finalexam2022.databinding.ActivityMyBroadcastReceiverBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyBroadcastReceiver : AppCompatActivity() {
    lateinit var binding: ActivityMyBroadcastReceiverBinding
    lateinit var broadcastReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBroadcastReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPermission()
        initLayout()
        checkSettingOverlayWindow(intent) // activity create 될 때 intent 넘어옴.
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkSettingOverlayWindow(intent) // activity create 될 때 intent 넘어옴.
    }

    fun getMessage(intent : Intent?){ // nullable
        if(intent != null){
            if(intent.hasExtra("msgSender") and intent.hasExtra("msgBody")) {
                val smsSender = intent.getStringExtra("msgSender")
                val smsBody = intent.getStringExtra("msgBody")
                Toast.makeText(this, "보낸 번호 : $smsSender \n $smsBody", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun checkSettingOverlayWindow(intent: Intent?){
        if(Settings.canDrawOverlays(this)){
            getMessage(intent)
        }else{
            val overlayIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            requestSettingLauncher.launch(overlayIntent)
        }
    }

    val requestSettingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(Settings.canDrawOverlays(this)){
            getMessage(this.intent)
        }else{
            Toast.makeText(this, "권한 승인이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it){
            initPermission()
        }else{
            Toast.makeText(this, "권한 승인이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun permissionAlertDlg(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("반드시 문자 수신 권한이 허용되어야 합니다.")
            .setTitle("권한 체크")
            .setPositiveButton("OK"){
                    _,_ ->
                requestPermissionLauncher.launch(android.Manifest.permission.RECEIVE_SMS)
            }
            .setNegativeButton("Cancel"){
                    dlg,_ -> dlg.dismiss()
            }
        val dlg = builder.create()
        dlg.show()
    }

    private fun initPermission() {
        when{
            (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)->{
                Toast.makeText(this, "문자 수신 동의함", Toast.LENGTH_SHORT).show()
            }

            //명시적으로 거부
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.RECEIVE_SMS)->{
                permissionAlertDlg()
            }

            //처음 실행
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.RECEIVE_SMS)
            }
        }
    }

    private fun initLayout() {
        broadcastReceiver = object: BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                if(p1 != null){
                    if(p1.action.equals(Intent.ACTION_POWER_CONNECTED)){
                        Toast.makeText(p0,"충전 시작", Toast.LENGTH_SHORT).show()
                    }else if(p1.action.equals(Intent.ACTION_POWER_DISCONNECTED)){
                        Toast.makeText(p0,"충전 해제", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }
}

class MyReceiver : BroadcastReceiver() {
    val pattern1 = Regex("""^\d{2}/\d{2}\s\d{2}:\d{2}""") // 월/일/시:분  로 시작하는(^)
    val pattern2 = Regex("""\d{3}원$""") // ~원 으로 끝나는($)
    val scope = CoroutineScope(Dispatchers.IO)
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        val pendingResult = goAsync() // pendingResult 의 finish 가 호출되기 전까지는 receiver 객체가 지워지지 않는다.
        scope.launch {
            if(intent.action.equals("android.provider.Telephony.SMS_RECEIVED")){
                val bundle = intent.extras
                val objects = bundle?.get("pdus") as Array<Any> // key 값이 pdus, any 타입 배열 형태임.
                val sms = objects[0] as ByteArray
                val format = bundle.getString("format")
                val message = SmsMessage.createFromPdu(sms, format) // sms message 객체
                val msg = message.messageBody
                if(msg.contains("건국카드")){
                    val tmpstr = msg.split("\n")
                    var result = false
                    for(str in tmpstr.subList(1, tmpstr.size)){
                        if(pattern1.containsMatchIn(str) && pattern2.containsMatchIn(str)){
                            result = true
                            break
                        }
                    }
                    if(result){
                        val newIntent = Intent(context, MyBroadcastReceiver::class.java)
                        newIntent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        newIntent.putExtra("msgSender", message.originatingAddress) // 발신자
                        newIntent.putExtra("msgBody",message.messageBody) // message 내용
                        context.startActivity(newIntent)
                    }
                }
            }
        }
        pendingResult.finish()
    }
}