package com.example.testapp.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.testapp.model.BluetoothModel

class MainViewModel : BaseViewModel(){

    companion object{
        const val EVENT_BLUETOOTH_DISCOVER = 22212
        const val EVENT_BLUETOOTH_CONNECT = 22213
        const val EVENT_BLUETOOTH_ON = 22214
    }

    //Save the selected Bluetooth here
    private val _bluetoothList = MutableLiveData<List<BluetoothModel>>()
    var bluetoothList: MutableLiveData<List<BluetoothModel>> = _bluetoothList

    //bluetooth Fragment event
    fun onDiscoverBluetoothButtonClick(){
        Log.d("DEV_DEBUG","찾기 버튼 클릭")
        viewEvent(EVENT_BLUETOOTH_DISCOVER)
    }

    fun onConnectBluetoothButtonClick(){
        Log.d("DEV_DEBUG","연결 버튼 클릭")
        viewEvent(EVENT_BLUETOOTH_CONNECT)
    }

    fun onBluetoothOnButtonClick(){
        Log.d("DEV_DEBUG","블루투스 활성화 버튼 클릭")
        viewEvent(EVENT_BLUETOOTH_ON)
    }


    fun addBluetooth(list: List<BluetoothModel>){
        _bluetoothList.value = list
    }
}