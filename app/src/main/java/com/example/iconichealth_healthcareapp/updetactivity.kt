package com.example.iconichealth_healthcareapp

import android.R
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.iconichealth_healthcareapp.databinding.ActivityAdddataBinding
import com.example.iconichealth_healthcareapp.databinding.ActivityUpdetactivityBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class updetactivity : AppCompatActivity() {

    lateinit var imguri: String
    lateinit var key: String
    lateinit var binding: ActivityUpdetactivityBinding

    var imageUri: Uri? = null

    var list = arrayListOf<DBCategory>()
    var data = arrayOf<String>("Select")
    var category :String = null.toString()
    var cid : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdetactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var name  = intent.getStringExtra("name")
        var description  = intent.getStringExtra("description")
        var offer  = intent.getStringExtra("offer")
        var price  = intent.getStringExtra("price")
        imguri  = intent.getStringExtra("imguri").toString()
        key  = intent.getStringExtra("key").toString()
        
        binding.productnameUpdet.setText(name)
        binding.productdescriptionUpdet.setText(description)
        binding.productofferUpdet.setText(offer)
        binding.productpriceUpdet.setText(price)
        Glide.with(this).load(imguri).into(binding.imageView)






        binding.imageView.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
        }

        binding.updetBtn.setOnClickListener {
            uplodeImgfirebase(imguri)
            var intent = Intent(this,Homepage::class.java)
            startActivity(intent)
        }
        readData()

    }

    private fun readData(){

        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseReference = firebaseDatabase.reference

        databaseReference.child("Category").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()
                data = emptyArray()

                for(x in snapshot.children){
                    var id = x.child("id").getValue().toString()
                    var cat = x.child("cat").getValue().toString()

                    var category = DBCategory(id,cat)

                    list.add(category)

                    data += x.child("cat").getValue().toString()
                }

                setupSpinner(data)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setupSpinner(data: Array<String>){

//        val arrayAdapter = SpinnerAdapter(this, data)
        var arrayAdapter = ArrayAdapter(this, R.layout.simple_spinner_item,data)
        binding.caragorySpinnerupdet.adapter = arrayAdapter
        arrayAdapter.notifyDataSetChanged()

        binding.caragorySpinnerupdet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                category = data[position]

                cid = position+1

                Log.e("TAG", "onItemSelected: $category", )

                Toast.makeText(this@updetactivity, "${data[position]}", Toast.LENGTH_SHORT).show()

            }

        }

    }

    private fun uplodeImgfirebase(uri:String) {
        var file  = File(imageUri.toString())

        var storage  = Firebase.storage
        var storageref = storage.reference

        var uplodeTask = storageref.child("${file.name}")

        if(imageUri!=null) {
                uplodeTask.putFile(imageUri!!).addOnSuccessListener { res ->

                    uplodeTask.downloadUrl.addOnSuccessListener { res ->
                        if (res != null) {
                            imageUri  = res
                            var m1 = DatabaseModel(
                                binding.productnameUpdet.text.toString(),
                                binding.productpriceUpdet.text.toString(),
                                binding.productdescriptionUpdet.text.toString(),
                                binding.productofferUpdet.text.toString(),
                                imageUri.toString()
                            )
                            insertData(m1)
                        }
                        Toast.makeText(this, "Sucess", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { error ->
                Toast.makeText(this, "Fail" + "${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            var m1 = DatabaseModel(
                binding.productnameUpdet.text.toString(),
                binding.productpriceUpdet.text.toString(),
                binding.productdescriptionUpdet.text.toString(),
                binding.productofferUpdet.text.toString(),
                uri.toString()
            )
            insertData(m1)
            Toast.makeText(this, "Sucess", Toast.LENGTH_SHORT).show()
        }
    }

    private fun insertData(m1: DatabaseModel) {
        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseReference = firebaseDatabase.reference
        databaseReference.child("PRODUCT").child("${key}").setValue(m1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data?.data
            binding.imageView.setImageURI(imageUri)
        }
    }

}