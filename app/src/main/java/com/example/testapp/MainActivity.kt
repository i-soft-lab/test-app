package com.example.testapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.testapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    /*
    BluetoothManager:
    BluetoothAdapter 객체를 획득할 때 사용하는 클래스. 전체적인 블루투스 관리
     */
    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    /*
    모든 블루투스 상호작용의 진입점.
     */
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

        if(bluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support Bluetooth.", Toast.LENGTH_SHORT).show()
            finish()
        }

        selectFragment();
    }

    fun setActivate() {
        bluetoothAdapter?.let {
            // 비활성화 상태라면
            if (!it.isEnabled) {
                // 활성화 요청
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activityResultLauncher.launch(intent)
            } else { // 활성 상태라면
                Toast.makeText(this, "It is already active.", Toast.LENGTH_SHORT).show()
            }
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