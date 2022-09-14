package com.example.iconichealth_healthcareapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iconichealth_healthcareapp.databinding.ActivityHomepageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class Homepage : AppCompatActivity() {

    lateinit var databaseReference2: DatabaseReference
    lateinit var dialog: Dialog
    lateinit var binding  : ActivityHomepageBinding

    var list  = arrayListOf<DatabaseModelread>()
    var cat:Int? = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference2 = firebaseDatabase.reference

        binding.addcatImg.setOnClickListener {



            dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_item)

            dialog.show()

            var cat_id = dialog.findViewById<TextView>(R.id.cat_id)
            var cat_name = dialog.findViewById<EditText>(R.id.cat_name)
            var Add_Btn = dialog.findViewById<Button>(R.id.Add_Btn)


            databaseReference2.child("Category").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (x in snapshot.children) {
                        var ctegory = x.child("id").getValue().toString()
                        cat = ctegory.toInt()

                    }
                    cat_id.text = (cat!!+1).toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TAG", "onDataChange: ${error.message}")

                }
            })

            Add_Btn.setOnClickListener{
                if(cat_name.length() == 0){
                    cat_name.error = "Name"
                }
                else
                {
                    var firebaseDatabase = FirebaseDatabase.getInstance()
                    var databaseReference3 = firebaseDatabase.reference

                    var dbCatagory = DBCategory(cat_id.text.toString(),cat_name.text.toString())

                    databaseReference3.child("Category").push().setValue(dbCatagory)
                    dialog.dismiss()
                }
                Toast.makeText(this, "Category Added Succesfully", Toast.LENGTH_SHORT).show()
            }
        }

        binding.logoutImg.setOnClickListener {
            var firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
            var firebase = Firebase
            firebase.auth.signOut()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.addImg.setOnClickListener {
            var intent = Intent(this,adddata::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupRv() {

        var myadepter = MyAdepter(this,list)
        var layoutManager = LinearLayoutManager(this)
        binding.rvview.adapter = myadepter
        binding.rvview.layoutManager = layoutManager

    }

    fun readData()
    {
        var firebaseDatabase = FirebaseDatabase.getInstance()
        var databaseReference = firebaseDatabase.reference

        databaseReference.child("PRODUCT").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()
                for (x in snapshot.children){
                    var name  = x.child("name").getValue().toString()
                    var description  = x.child("description").getValue().toString()
                    var price  = x.child("price").getValue().toString()
                    var offer  = x.child("offer").getValue().toString()
                    var uri = x.child("imguri").getValue().toString()
                    var key  = x.key.toString()

                    var m1 = DatabaseModelread(name,price,description,offer,key,uri)
                    list.add(m1)

                    Log.e("TAG", "onDataChange: ${list}")

                }
                setupRv()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onDataChange: ${error.message}")

            }
        })
    }



    override fun onStart() {
        super.onStart()
        readData()
    }
}

class DatabaseModelread(
    var name: String,
    var price: String,
    var description: String,
    var offer: String,
    var key: String,
    var imguri : String
)
