package com.example.testapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.databinding.ItemBluetoothBinding
import com.example.testapp.model.BluetoothModel
import com.example.testapp.viewModel.MainViewModel

class BluetoothListAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<BluetoothListAdapter.BluetoothItemViewHolder>() {

    var bluetoothList: List<BluetoothModel> = listOf()
        set(v) {
            field = v
            notifyDataSetChanged()
        }

    inner class BluetoothItemViewHolder(private val binding: ItemBluetoothBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bluetoothModel: BluetoothModel) {
            binding.textBluetoothName.text = bluetoothModel.deviceName
            binding.textBluetoothAddress.text = bluetoothModel.deviceMacAddress
            bluetoothModel.deviceName?.let { Log.d("DEV_DEBUG", it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothItemViewHolder {
        return BluetoothItemViewHolder(
            ItemBluetoothBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BluetoothListAdapter.BluetoothItemViewHolder, position: Int) {
        holder.bind(bluetoothList[position])
    }

    override fun getItemCount(): Int = bluetoothList.size
}