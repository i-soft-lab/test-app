package com.example.testapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel(){

    //Save the selected Bluetooth here
    private val _bluetoothName = MutableLiveData<Int>()
    val bluetoothName: LiveData<Int> = _bluetoothName

    //bluetooth Fragment event
    fun onDiscoverButtonClick(){
        Log.d("djaljflk","찾기 버튼 클릭")
    }

    fun onConnectButtonClick(){
        Log.d("djaljflk","연결 버튼 클릭")
    }
}