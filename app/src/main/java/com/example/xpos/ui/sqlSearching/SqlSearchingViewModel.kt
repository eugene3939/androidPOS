package com.example.xpos.ui.sqlSearching

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SqlSearchingViewModel : ViewModel() {
    // 在這裡添加 ViewModel 的邏輯
    private val _text = MutableLiveData<String>().apply {
        value = "This is sqlManageShow Fragment"
    }
    val text: LiveData<String> = _text
}

