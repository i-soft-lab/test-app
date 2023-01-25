package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.adapter.BluetoothListAdapter
import com.example.testapp.databinding.FragmentBluetoothBinding
import com.example.testapp.viewModel.MainViewModel

class BluetoothFragment : Fragment() {

    // 뷰모델 불러오기
    private val mainViewModel: MainViewModel by activityViewModels()
    // 바인딩 선언
    private var binding: FragmentBluetoothBinding? = null
    private lateinit var bluetoothListAdapter: BluetoothListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //바인딩 초기화
        val bluetoothFragmentBinding = FragmentBluetoothBinding.inflate(inflater, container, false)
        binding = bluetoothFragmentBinding
        return bluetoothFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            // Specify the fragment as the lifecycle owner
            lifecycleOwner = viewLifecycleOwner

            // Assign the view model to a property in the binding class
            viewModel = mainViewModel
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        bluetoothListAdapter = BluetoothListAdapter(mainViewModel)

        binding?.listviewDevices?.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = bluetoothListAdapter
        }

        mainViewModel.bluetoothList.observe(viewLifecycleOwner){
            Log.d("DEV_DEBUG","리스트 업데이트됨")
            bluetoothListAdapter.bluetoothList = it
        }
    }
}