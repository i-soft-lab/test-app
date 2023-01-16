package com.example.testapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.testapp.databinding.FragmentBluetoothBinding
import com.example.testapp.viewModel.MainViewModel

class BluetoothFragment : Fragment() {

    // 뷰모델 불러오기
    private val mainViewModel: MainViewModel by activityViewModels()
    // 바인딩 선언
    private var binding: FragmentBluetoothBinding? = null

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
    }
}