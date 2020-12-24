package com.example.kotlin_hw_sqlite

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var dbrw: SQLiteDatabase

    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //宣告Adapter並連結ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
        //取得資料庫實體
        dbrw = MyDBHelper(this).writableDatabase

        btn_query.setOnClickListener {
            val c = dbrw.rawQuery(if(ed_book.length()<1) "SELECT * FROM myTable"
            else "SELECT * FROM myTable WHERE book LIKE '${ed_book.text}'",null)
            //從第一筆開始輸出
            c.moveToFirst()
            //清空舊資料
            items.clear()
            Toast.makeText(this@MainActivity,"共有${c.count}筆資料",Toast.LENGTH_SHORT).show()
            for (i in 0 until c.count) {
                items.add("書名:${c.getString(0)}\t\t\t\t價格:${c.getInt(1)}")
                //移動到下一筆
                c.moveToNext()
            }
            //更新列表資料
            adapter.notifyDataSetChanged()
            //關閉Cursor
            c.close()
        }


        btn_insert.setOnClickListener {
            //判斷是否沒有填入書名或價格
            if (ed_book.length()<1 || ed_price.length()<1)
                Toast.makeText(this@MainActivity,"欄位請勿留空",Toast.LENGTH_SHORT).show()
            else
                try{
                    //新增一筆book與price資料進入myTable資料表
                    dbrw.execSQL("INSERT INTO myTable(book, price) VALUES(?,?)",
                        arrayOf<Any?>(ed_book.text.toString(), ed_price.text.toString()))
                    Toast.makeText(this@MainActivity,"新增書名${ed_book.text}"+
                            "價格${ed_price.text}",Toast.LENGTH_SHORT).show()
                    ed_book.setText("")
                    ed_price.setText("")
                }catch (e: Exception){
                    Toast.makeText(this@MainActivity,"新增失敗:$e",Toast.LENGTH_SHORT).show()
                }
        }

        btn_update.setOnClickListener {
            //判斷是否沒有填入書名或價格
            if (ed_book.length()<1 || ed_price.length()<1)
                Toast.makeText(this@MainActivity,"欄位請勿留空",Toast.LENGTH_SHORT).show()
            else
                try{
                    //更新book欄位為輸入字串（ed_book）的資料的price欄位數值
                    dbrw.execSQL("UPDATE myTable SET price = ${ed_price.text} WHERE book LIKE '${ed_book.text}'")
                    Toast.makeText(this@MainActivity,"更新書名${ed_book.text} " +
                            "價格${ed_price.text}",Toast.LENGTH_SHORT).show()
                    ed_book.setText("")
                    ed_price.setText("")
                }catch (e: Exception){
                    Toast.makeText(this@MainActivity,"更新失敗:$e",Toast.LENGTH_SHORT).show()
                }
        }

        btn_delete.setOnClickListener {
            //判斷是否沒有填入書名
            if (ed_book.length()<1)
                Toast.makeText(this@MainActivity,"書名請勿留空",Toast.LENGTH_SHORT).show()
            else
                try{
                    //從myTable資料表刪除book欄位為輸入字串（ed_book）的資料
                    dbrw.execSQL("DELETE FROM myTable WHERE book LIKE '${ed_book.text}'")
                    Toast.makeText(this@MainActivity,"刪除書名${ed_book.text}",Toast.LENGTH_SHORT).show()
                    ed_book.setText("")
                    ed_price.setText("")
                }catch (e: Exception){
                    Toast.makeText(this@MainActivity,"刪除失敗:$e",Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        //關閉資料庫
        dbrw.close()
    }
}