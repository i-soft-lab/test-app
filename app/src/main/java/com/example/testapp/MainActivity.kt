package com.example.testapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.testapp.databinding.ActivityMainBinding
import com.example.testapp.viewModel.MainViewModel


class MainActivity : AppCompatActivity() {
    private var PERMISSION_REQUEST_CODE_S = 101
    private var PERMISSION_REQUEST_CODE = 100
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels()

    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this, "bluetooth activate", Toast.LENGTH_SHORT).show()
            } else if (it.resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bluetoothAdapterNullCheck()
        manageViewModelEvent()
        bleInitialize()

        selectFragment()
    }

    private fun manageViewModelEvent(){
        mainViewModel.viewEvent.observe(this) {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    MainViewModel.EVENT_BLUETOOTH_ON -> {
                        setActivate();
                    }
                }
            }
        }
    }

    private fun bleInitialize(){
        // 런타임 권한 확인
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

    private fun requestBlePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),PERMISSION_REQUEST_CODE_S)
        }
        else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE)
        }
    }

    fun setActivate() {
        if(bluetoothAdapter?.isEnabled == false){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(enableBtIntent, 0)
        }else{
            Toast.makeText(this, "It is already active.", Toast.LENGTH_SHORT).show()

        }
    }

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