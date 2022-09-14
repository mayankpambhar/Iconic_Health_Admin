package com.example.iconichealth_healthcareapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

data class MyAdepter(val activity: Activity, val list: ArrayList<DatabaseModelread>) : RecyclerView.Adapter<MyAdepter.ViewData>() {

    class ViewData(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var prdname_txt = itemView.findViewById<TextView>(R.id.prdname_txt)
        var prdprice_txt = itemView.findViewById<TextView>(R.id.prdprice_txt)
        var prddescription_txt = itemView.findViewById<TextView>(R.id.prddescription_txt)
        var prdoffer_txt = itemView.findViewById<TextView>(R.id.prdoffer_txt)
        var delet_txt = itemView.findViewById<TextView>(R.id.delet_txt)
        var updet_txt = itemView.findViewById<TextView>(R.id.updet_txt)
        var product_img = itemView.findViewById<ImageView>(R.id.product_img)
        var spinner_txt = itemView.findViewById<TextView>(R.id.spinner_txt)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewData {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)
        return  ViewData(view)
    }

    override fun onBindViewHolder(holder: ViewData, position: Int) {


        holder.prdname_txt.text = list.get(position).name
        holder.prdprice_txt.text = list.get(position).price
        holder.prddescription_txt.text = list.get(position).description
        holder.prdoffer_txt.text = list.get(position).offer

        Glide.with(activity).load(list.get(position).imguri).into(holder.product_img)

//        holder.product_img.setImageResource(R.drawable.google)


        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseReference = firebaseDatabase.reference

        holder.delet_txt.setOnClickListener {
            databaseReference.child("PRODUCT").child("${list.get(position).key}").removeValue()
            list.clear()
        }

        holder.updet_txt.setOnClickListener {


            var intent  = Intent(activity,updetactivity::class.java)
            intent.putExtra("name",list[position].name)
            intent.putExtra("description",list[position].description)
            intent.putExtra("offer",list[position].offer)
            intent.putExtra("price",list[position].price)
            intent.putExtra("imguri",list[position].imguri)
            intent.putExtra("key",list[position].key)
            activity.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
      return list.size
    }



}