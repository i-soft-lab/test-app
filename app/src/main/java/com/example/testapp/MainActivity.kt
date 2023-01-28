package com.example.testapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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


//private lateinit var broadcastReceiver: BroadcastReceiver

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels()
    private val discoverBlueTootlList = mutableListOf<BluetoothModel>()

    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    discoverBlueTootlList.add(
                        BluetoothModel(
                            device?.name ?: "이름없는 기기",
                            device?.address
                        )
                    )
                    Log.d("DEV_DEBUG", "${device?.name}, ${device?.address}")
                }
            }
            mainViewModel.addBluetooth(discoverBlueTootlList)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    /**
     * A function that handles events from a view model to activity.
     */
    private fun manageViewModelEvent() {
        mainViewModel.viewEvent.observe(this) {
            it.getContentIfNotHandled()?.let { event ->
                when (event) {
                    MainViewModel.EVENT_BLUETOOTH_ON -> {
                        setActivate()
                        pairedDevice()
                    }
                    MainViewModel.EVENT_BLUETOOTH_DISCOVER -> {
                        discoverBluetooth()
                    }
                }
            }
        }
    }

    /**
     * Bluetooth Runtime Permission Check
     */
    private fun bleInitialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                requestBlePermissions()
                return
            }
        } else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestBlePermissions()
                return
            }
        }
    }

    private fun pairedDevice() {
        if (bluetoothAdapter?.isEnabled == true) {
            //noinspection MissingPermission
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            mainViewModel.bluetoothList.value = pairedDevices?.map { device ->
                Log.d("djaljflk", "${device.name}")
                //noinspection MissingPermission
                BluetoothModel(device.name, device.address)
            } as MutableList<BluetoothModel>?
        }
    }

    private fun requestBlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 100
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    fun setActivate() {
        if (bluetoothAdapter?.isEnabled == false) {
            //noinspection MissingPermission
            bluetoothAdapter?.enable()
            Toast.makeText(this, "블루투스 활성화", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "블루투스가 이미 활성화되어있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Check to see if Bluetooth is supported
     */
    private fun bluetoothAdapterNullCheck() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun discoverBluetooth() {
        bluetoothAdapter?.let {
            bluetoothAdapter?.let {
                // 블루투스가 활성화 상태라면
                if (it.isEnabled) {
                    // 현재 검색중이라면
                    //noinspection MissingPermission
                    if (it.isDiscovering) {
                        // 검색 취소
                        //noinspection MissingPermission
                        it.cancelDiscovery()
                        Toast.makeText(this, "기기검색이 중단되었습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    discoverBlueTootlList.clear()
                    // 검색시작
                    //noinspection MissingPermission
                    it.startDiscovery()
                    Toast.makeText(this, "기기 검색을 시작하였습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "블루투스가 비활성화되어 있습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    private fun selectFragment() {
        changeFragment(BluetoothFragment())
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bluetooth -> {
                    changeFragment(BluetoothFragment())
                }
                R.id.wifi -> {
                    changeFragment(WifiFragment())
                }
                R.id.chart -> {
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