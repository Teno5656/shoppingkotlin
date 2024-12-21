package com.example.shoppingkotlinmvvm

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppingkotlinmvvm.databinding.RegNumberBinding
import java.util.*

class Register: AppCompatActivity() {

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var binding: RegNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btConfirm.setOnClickListener {
            binding.edName.text.toString()
            System.out.println("aaa" + binding.edName.text.toString())
        }

        //取得現在的日期
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR) //年
        val month = cal.get(Calendar.MONTH) //月
        val day = cal.get(Calendar.DAY_OF_MONTH) //日

        binding.dtPicker.setText("$year - $month - $day")  //日期初始值

        datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
            run {
                val date = "${setDateFormat(year, month, day)}"
                binding.dtPicker.text = date  //取得選定的日期給日期編輯框
            }
        }, year, month, day)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()  //設定日期最大值

        //註冊頁日期功能
        binding.dtPicker.setOnClickListener {
            datePickerDialog.show()
        }
    }

    fun setDateFormat(year: Int, month: Int, day: Int): String {
        return "$year-${month + 1}-$day"
    }
}