package com.example.retrofitgetapi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofit_getapi.R
import com.example.retrofitgetapi.Fitur.Edit
import com.example.retrofitgetapi.Network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RVAdapter(
    private val context: Context,
    private val dataList: ArrayList<Mahasiswa>
) : RecyclerView.Adapter<RVAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvNim = view.findViewById<TextView>(R.id.nimTextView)
        val tvNama = view.findViewById<TextView>(R.id.namaTextView)
        val tvTelp = view.findViewById<TextView>(R.id.telpTextView)
        val cvMain = view.findViewById<CardView>(R.id.cv_main)
        val btDelete = view.findViewById<AppCompatImageButton>(R.id.btnDellete)
        val btEdit = view.findViewById<AppCompatImageButton>(R.id.btnEdit)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvNim.text = dataList[position].nim
        holder.tvNama.text = dataList[position].nama
        holder.tvTelp.text = dataList[position].telepon

        holder.cvMain.setOnClickListener {
            Toast.makeText(context, dataList[position].nama, Toast.LENGTH_SHORT).show()
        }

        holder.btDelete.setOnClickListener {
            val nim = dataList[position].nim
            this.remoteDeleteMahasiswa(nim)
        }
        holder.btEdit.setOnClickListener{
            val intent = Intent(context, Edit::class.java)
            val bundle = Bundle()
            bundle.putString("nim", dataList[position].nim)
            bundle.putString("nama", dataList[position].nama)
            bundle.putString("telepon", dataList[position].telepon)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = dataList.size

    fun setData(data: List<Mahasiswa>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    private fun remoteDeleteMahasiswa(nim: String) {
        ApiClient.apiService.deleteMahasiswa(nim).enqueue(object : Callback<ResponseDataDeleteMahasiswa> {
            override fun onResponse(
                call: Call<ResponseDataDeleteMahasiswa>,
                response: Response<ResponseDataDeleteMahasiswa>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val data = apiResponse?.data
                    if (data != null) {
                        // Find the index of the deleted item in the dataList
                        val index = dataList.indexOfFirst { it.nim == nim }
                        if (index != -1 && index < dataList.size) {
                            // Remove the item from the dataList
                            dataList.removeAt(index)
                            // Notify the adapter that the item has been removed
                            notifyItemRemoved(index)
                            Toast.makeText(context, "Data terhapus", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDataDeleteMahasiswa>, t: Throwable) {
                Log.d("Error", t.stackTraceToString())
            }
        })
    }



}