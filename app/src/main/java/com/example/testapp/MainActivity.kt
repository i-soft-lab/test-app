package com.example.testapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.testapp.databinding.ActivityMainBinding
import com.example.testapp.model.BluetoothModel
import com.example.testapp.viewModel.MainViewModel


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels()

    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bluetoothAdapterNullCheck()
        manageViewModelEvent()
        bleInitialize()
        pairedDevice()

        selectFragment()
    }

    /**
     * A function that handles events from a view model to activity.
     */
    private fun manageViewModelEvent(){
        mainViewModel.viewEvent.observe(this) {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    MainViewModel.EVENT_BLUETOOTH_ON -> {
                        setActivate()
                    }
                    MainViewModel.EVENT_BLUETOOTH_DISCOVER ->{

                    }
                }
            }
        }
    }

    /**
     * Bluetooth Runtime Permission Check
     */
    private fun bleInitialize(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestBlePermissions()
                return
            }
        }
        else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestBlePermissions()
                return
            }
        }
    }

    private fun pairedDevice(){
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        mainViewModel.bluetoothList.value = pairedDevices?.map { device ->
            Log.d("djaljflk","${device.name}")
            BluetoothModel(device.name, device.address)
        }
    }

    private fun requestBlePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),100)
        }
        else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100)
        }
    }

    fun setActivate() {
        if(bluetoothAdapter?.isEnabled == false){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            bluetoothAdapter?.enable()
            Toast.makeText(this, "Turn on Bluetooth", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "It is already active.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Check to see if Bluetooth is supported
     */
    private fun bluetoothAdapterNullCheck(){
        if(bluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support Bluetooth.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun selectFragment(){
        changeFragment(BluetoothFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bluetooth ->{
                    changeFragment(BluetoothFragment())
                }
                R.id.wifi ->{
                    changeFragment(WifiFragment())
                }
                R.id.chart ->{
                    changeFragment(GdxFragment())
                }
            }
            true
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}