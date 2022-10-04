package com.example.munch.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.munch.Adapter.CategoriesAdapter
import com.example.munch.Adapter.PopularAdapter
import com.example.munch.Domain.Cart
import com.example.munch.Domain.Categories
import com.example.munch.Domain.Popular
import com.example.munch.ILoadPopularListener
import com.example.munch.Interface.ICartLoadListener
import com.example.munch.R
import com.example.munch.UpdateCartEvent
import com.example.munch.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.viewholder_categories.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() , ICartLoadListener, ILoadPopularListener {


    lateinit var cartLoadListener: ICartLoadListener
    lateinit var popularILoadPopularListener: ILoadPopularListener
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var mFirebaseAuth : FirebaseAuth

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun UpdateCartEvent(event: UpdateCartEvent)
    {
        countCartFromFirebase()
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoriesList: ArrayList<Categories>
    //private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var popularAdapter: PopularAdapter
    private lateinit var popularList: ArrayList<Popular>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFirebaseAuth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("name")

        findViewById<TextView>(R.id.tv_Name).text = "Hi, " + displayName

        // fetchData()
        fetchData1()
        // init()
        init1()
        countCartFromFirebase()

       /* imageView4.setOnClickListener {
            mFirebaseAuth.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }*/

        drinksRV.setOnClickListener {
            val intent = Intent(this,DrinksActivity::class.java)
                startActivity(intent)
        }
        pizzaRv.setOnClickListener {
            val intent = Intent(this,PizzasActivity::class.java)
            startActivity(intent)
        }
        burgerRv.setOnClickListener {
            val intent = Intent(this,BurgerActivity::class.java)
            startActivity(intent)
        }

    }

      private fun countCartFromFirebase() {
          val cartModels: MutableList<Cart> = ArrayList()
          FirebaseDatabase.getInstance()
              .getReference("Cart")
              .child("UNIQUE_USER_ID")
              .addListenerForSingleValueEvent(object : ValueEventListener {
                  override fun onDataChange(snapshot: DataSnapshot) {
                      for (cartSnapshot in snapshot.children)
                      {
                          val cart = cartSnapshot.getValue(Cart::class.java)
                          cart!!.key = cartSnapshot.key
                          cartModels.add(cart)
                      }
                      cartLoadListener.onLoadCartSuccess(cartModels)
                  }

                  override fun onCancelled(error: DatabaseError) {
                      cartLoadListener.onLoadCartFailed(error.message)
                  }
              })
      }

    private fun fetchData1() {
        val drinkModels: MutableList<Popular> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Popular")
            .addListenerForSingleValueEvent(object : ValueEventListener {


                /* FirebaseFirestore.getInstance()
            .collection("Popular")
            .get()

            .addOnSuccessListener { documents ->

                for (document in documents){
                    val popular =documents.toObjects(Popular::class.java)
                    binding.popularRv.adapter=PopularAdapter(this,popular!!, cartLoadListener)
                }
            }
            .addOnFailureListener {
            }*/
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        for (drinkSnapshot in snapshot.children)
                        {
                            val drinkModel = drinkSnapshot.getValue(Popular::class.java)
                            drinkModel!!.key = drinkSnapshot.key
                            drinkModels.add(drinkModel)
                        }
                        popularILoadPopularListener.onPopularLoadSuccess(drinkModels)
                    }
                    else{
                        popularILoadPopularListener.onPopularLoadFailed("Item does not exists")
                }

            }

                override fun onCancelled(error: DatabaseError) {
                    popularILoadPopularListener.onPopularLoadFailed(error.message)
                }
            })
    }




   /* private fun fetchData() {
        FirebaseFirestore.getInstance()
            .collection("Categories")
            .get()

            .addOnSuccessListener { documents ->

                for (document in documents){
                    val categories =documents.toObjects(Categories::class.java)
                    binding.categoriesRv.adapter=CategoriesAdapter(this,categories)
                }
            }
            .addOnFailureListener {
            }



        recyclerView = findViewById(R.id.categoriesRv)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        categoriesList = ArrayList()



        addDataToList()


        categoriesAdapter = CategoriesAdapter(categoriesList)
        recyclerView.adapter = categoriesAdapter

    }*/



   /* private fun init() {
        recyclerView = findViewById(R.id.categoriesRv)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        categoriesList = ArrayList()
        addDataToList()

       val categoriesAdapter = CategoriesAdapter(categoriesList)
        recyclerView.adapter = categoriesAdapter
        /* categoriesAdapter.setOnItemClickListener(object : CategoriesAdapter.onClickListener{
             override fun onItemClick(position: Int) {
                 /* val intent = Intent(this@MainActivity, DrinksActivity::class.java)
                  intent.putExtra("Drink",categoriesList[position].categoriesName)
                  intent.putExtra("Image",categoriesList[position].categoriesImage)
                  startActivity(intent) */

             }

        })*/
    }

    private fun addDataToList() {
        categoriesList.add(Categories(R.drawable.cat_1, "Pizza"))
        categoriesList.add(Categories(R.drawable.cat_2, "Burger"))
        categoriesList.add(Categories(R.drawable.cat_3, "Hotdog"))
        categoriesList.add(Categories(R.drawable.cat_4, "Drink"))
        categoriesList.add(Categories(R.drawable.cat_5, "Doughnut"))
    }*/


private fun init1(){
        recyclerView = findViewById(R.id.popularRv)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        popularList = ArrayList()

        cartLoadListener = this
        popularILoadPopularListener = this

        cart_Btn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onLoadCartSuccess(cartModelList: List<Cart>) {
        var cartSum = 0
        for(cart in cartModelList!!) cartSum+= cart!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        //Snackbar.make(mainLayout, message!!,Snackbar.LENGTH_LONG).show()
    }

    override fun onPopularLoadSuccess(popularList: List<Popular>?) {
        val adapter = PopularAdapter(this,popularList!!, cartLoadListener)
        popularRv.adapter = adapter
    }

    override fun onPopularLoadFailed(message: String?) {
        Snackbar.make(mainLayout, message!!,Snackbar.LENGTH_LONG).show()
    }


}
/*
    private fun addDataToList1() {
        popularList.add(Popular(R.drawable.pizza1,"Pepperoni Pizza","3,800"))
        popularList.add(Popular(R.drawable.burger,"Cheesse Burger","2,800"))
        popularList.add(Popular(R.drawable.pizza3,"Vegetable Pizza","3,300"))

    } */




