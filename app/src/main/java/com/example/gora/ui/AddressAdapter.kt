package com.example.gora.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gora.R
import com.example.gora.START_ADDRESS

class AddressAdapter(private var addressList: List<String>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapter.AddressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressAdapter.AddressViewHolder, position: Int) {
        val address = addressList[position]
        holder.bind(address)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.tv_search_item)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val address = addressList[position]
                    onItemClick(address)
                }
            }
        }



        fun bind(address: String) {
            textView.text = address
        }
    }

    fun updateAddressList(newAddressList: List<String>) {
        addressList = newAddressList
    }



}