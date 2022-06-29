package com.example.finalexam2022

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.finalexam2022.databinding.ActivityMySqliteBinding
import java.io.FileOutputStream

class MySQLite : AppCompatActivity() {
    lateinit var binding : ActivityMySqliteBinding
    lateinit var myDBHelper: MyDBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMySqliteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDB()
        init()
        getALLRecord()
    }

    private fun initDB() {
        val dbfile = getDatabasePath("mydb.db")
        if(!dbfile.parentFile.exists()){
            dbfile.parentFile.mkdir()
        }
        if(!dbfile.exists()){
            val file = resources.openRawResource(R.raw.mydb)
            val fileSize = file.available()
            val buffer = ByteArray(fileSize)
            file.read(buffer)
            file.close()
            dbfile.createNewFile()
            val output = FileOutputStream(dbfile)
            output.write(buffer)
            output.close()
        }
    }

    fun getALLRecord(){
        myDBHelper.getALLRecord()
    }
    fun clearEditText(){
        binding.apply{
            pIdEdit.text.clear()
            pNameEdit.text.clear()
            pQuantityEdit.text.clear()
        }
    }
    private fun init(){
        myDBHelper = MyDBHelper(this)
        binding.apply{
            testsql.addTextChangedListener{
                val pname = it.toString()
                val result = myDBHelper.findProduct2(pname)

            }
            insertbtn.setOnClickListener {
                val name = pNameEdit.text.toString()
                val quantity = pQuantityEdit.text.toString().toInt()
                val product = Product(0,name,quantity)
                val result = myDBHelper.insertProduct(product)
                if(result){
                    getALLRecord()
                    Toast.makeText(this@MySQLite, "Data Insert SUCCESS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MySQLite, "Data Insert FAILED", Toast.LENGTH_SHORT).show()
                }
                clearEditText()

            }
            findbtn.setOnClickListener {
                val name = pNameEdit.text.toString()
                val result = myDBHelper.findProduct(name)
                if(result){
                    Toast.makeText(this@MySQLite, "RECORD FOUND", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MySQLite, "NO MATCH FOUND", Toast.LENGTH_SHORT).show()
                }
            }
            deletebtn.setOnClickListener {
                val pid = pIdEdit.text.toString()
                val result = myDBHelper.deleteProduct(pid)
                if(result){
                    Toast.makeText(this@MySQLite, "Data DELETE SUCCESS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MySQLite, "Data DELETE FAILED", Toast.LENGTH_SHORT).show()
                }
                getALLRecord()
                clearEditText()

            }
            updatebtn.setOnClickListener {
                val pid = pIdEdit.text.toString().toInt()
                val name = pNameEdit.text.toString()
                val quantity = pQuantityEdit.text.toString().toInt()
                val product = Product(pid,name,quantity)
                val result = myDBHelper.updateProduct(product)
                if(result){
                    getALLRecord()
                    Toast.makeText(this@MySQLite, "Data Update SUCCESS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@MySQLite, "Data Update FAILED", Toast.LENGTH_SHORT).show()
                }
                clearEditText()
            }
        }
    }
}

class MyDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "mydb.db"
        val DB_VERSION = 1
        val TABLE_NAME = "products"
        val PID = "pid"
        val PNAME = "pname"
        val PQUANTITY = "pquantity"
    }

    fun getALLRecord() {
        val strsql = "select * from $TABLE_NAME;"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)

        showRecord(cursor)
        cursor.close()
        db.close()

    }

    private fun showRecord(cursor: Cursor) {
        cursor.moveToFirst()
        val attrcount = cursor.columnCount // column 갯수
        val activity = context as MySQLite
        activity.binding.tableLayout.removeAllViewsInLayout()


        // 테이블의 타이틀 만드는 작업
        val tablerow = TableRow(activity)
        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        tablerow.layoutParams = rowParam
        val viewParam = TableRow.LayoutParams(0,100,1f)
        for(i in 0 until attrcount){
            val textView = TextView(activity)
            textView.layoutParams = viewParam
            textView.text = cursor.getColumnName(i)
            textView.setBackgroundColor(Color.LTGRAY)
            textView.textSize = 15.0f
            textView.gravity = Gravity.CENTER
            tablerow.addView(textView)
        }
        activity.binding.tableLayout.addView(tablerow)

        if(cursor.count == 0) return // 레코드 추가할 게 없을 때 Return

        // 테이블에 레코드 추가 작업
        do{
            val row = TableRow(activity)
            row.layoutParams = rowParam
            row.setOnClickListener{
                for(i in 0 until attrcount){
                    val textView = row.getChildAt(i) as TextView
                    when(textView.tag){
                        0 -> activity.binding.pIdEdit.setText(textView.text)
                        1 -> activity.binding.pNameEdit.setText(textView.text)
                        2 -> activity.binding.pQuantityEdit.setText(textView.text)
                    }
                }
            }
            for(i in 0 until attrcount){
                val textView = TextView(activity)
                textView.tag = i
                textView.layoutParams = viewParam
                textView.text = cursor.getString(i)
                textView.textSize = 13.0f
                textView.gravity = Gravity.CENTER
                row.addView(textView)
            }
            activity.binding.tableLayout.addView(row)
        }while(cursor.moveToNext())

    }

    fun insertProduct(product: Product):Boolean{
        Log.d("name1", product.pName)
        val values = ContentValues()
        values.put(PNAME,product.pName)
        values.put(PQUANTITY, product.pQuantity)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values) > 0
        db.close()
        return flag
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME("+
                "$PID integer primary key autoincrement, "+
                "$PNAME text, "+
                "$PQUANTITY integer);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun findProduct(name: String): Boolean {
        Log.d("name1", name)
        val strsql = "select * from $TABLE_NAME where $PNAME='$name';" // select * from product where pname = 'pname'
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        showRecord(cursor)
        cursor.close()
        db.close()
        return flag
    }
    // select * from product where pid = 'pid'
    fun deleteProduct(pid: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $PID='$pid';" // select * from product where pname = 'pname'
        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME, "$PID=?", arrayOf(pid))
        }
        cursor.close()
        db.close()
        return flag
    }

    fun updateProduct(product: Product): Boolean {
        val pid = product.pId
        val strsql = "select * from $TABLE_NAME where $PID='$pid';" // select * from product where pname = 'pname'
        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if(flag){
            cursor.moveToFirst()
            val values = ContentValues()
            values.put(PNAME,product.pName)
            values.put(PQUANTITY, product.pQuantity)
            db.update(TABLE_NAME, values,"$PID=?", arrayOf(pid.toString()))
        }
        cursor.close()
        db.close()
        return flag
    }

    //select * from product where pname like 'name%'
    fun findProduct2(name: String): Boolean {
        Log.d("name1", name)
        val strsql = "select * from $TABLE_NAME where $PNAME like '$name%';" // select * from product where pname = 'pname'
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        showRecord(cursor)
        cursor.close()
        db.close()
        return flag
    }
}

data class Product(var pId:Int, var pName: String, var pQuantity: Int)
