package com.example.iconichealth_healthcareapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import com.example.iconichealth_healthcareapp.databinding.ActivityAdddataBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class adddata : AppCompatActivity() {

    lateinit var binding: ActivityAdddataBinding

    val pickImage = 100
    var imageUri: Uri? = null

    var list = arrayListOf<DBCategory>()
    var data = arrayOf<String>("Select")
    var category :String = null.toString()
    var cid : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdddataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uplodeBtn.setOnClickListener {
            uplodeImgfirebase()
            var intent = Intent(this, Homepage::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageView.setOnClickListener {

                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
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
        var arrayAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,data)
        binding.categorySpinner.adapter = arrayAdapter
        arrayAdapter.notifyDataSetChanged()

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                category = data[position]

                cid = position+1

                Log.e("TAG", "onItemSelected: $category", )

                Toast.makeText(this@adddata, "${data[position]}", Toast.LENGTH_SHORT).show()

            }

        }

    }

    private fun uplodeImgfirebase() {
        var file  = File(imageUri.toString())

        var storage  = Firebase.storage
        var storageref = storage.reference

        var uplodeTask = storageref.child("${file.name}")
        uplodeTask.putFile(imageUri!!).addOnSuccessListener {
            res->

            uplodeTask.downloadUrl.addOnSuccessListener { res->
                if(res!=null)
                {
                    imageUri = res
                    var m1 = DatabaseModel(
                        binding.productnameEdt.text.toString(),
                        binding.productpriceEdt.text.toString(),
                        binding.productdescriptionEdt.text.toString(),
                        binding.productofferEdt.text.toString(),
                        imageUri.toString()
                    )
                    insertData(m1)
                }
                else{
                    Toast.makeText(this, "Uplode Image Pls", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, "Sucess", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener{ error->
            Toast.makeText(this, "Fail"+"${error.message}", Toast.LENGTH_SHORT).show()
        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.imageView.setImageURI(imageUri)
        }
    }


    private fun insertData(m1: DatabaseModel) {
        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseReference = firebaseDatabase.reference
        databaseReference.child("PRODUCT").push().setValue(m1)
    }


}

class DBCategory(val id: String,val cat: String) {


}


data class DatabaseModel(
    var name: String,
    var price: String,
    var description: String,
    var offer: String,
    val imguri: String
)