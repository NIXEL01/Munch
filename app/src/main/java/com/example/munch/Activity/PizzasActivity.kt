package com.example.munch.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.munch.Adapter.PizzasAdapter
import com.example.munch.Domain.Cart
import com.example.munch.Domain.Drinks
import com.example.munch.Interface.ICartLoadListener
import com.example.munch.Interface.IDrinkLoadListener
import com.example.munch.R
import com.example.munch.UpdateCartEvent
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_drinks.*
import kotlinx.android.synthetic.main.viewholder_categories.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PizzasActivity : AppCompatActivity(),IDrinkLoadListener,ICartLoadListener {

    lateinit var drinkLoadListener: IDrinkLoadListener
    lateinit var cartLoadListener: ICartLoadListener

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drinks)

        init()
        fetchDataFromFirebase()
        countCartFromFirebase()

        product_Tv.setText("Pizza").toString()


    }

    private fun countCartFromFirebase() {
        val cartModels: MutableList<Cart> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener{
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

    private fun fetchDataFromFirebase() {
        val drinkModels: MutableList<Drinks> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Pizza")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        for (drinkSnapshot in snapshot.children)
                        {
                            val drinkModel = drinkSnapshot.getValue(Drinks::class.java)
                            drinkModel!!.key = drinkSnapshot.key
                            drinkModels.add(drinkModel)
                        }
                        drinkLoadListener.onDrinkLoadSuccess(drinkModels)
                    }
                    else{
                        drinkLoadListener.onDrinkLoadFailed("Item does not exists")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    drinkLoadListener.onDrinkLoadFailed(error.message)
                }

            })
    }

    fun init() {

        drinkLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_drink.layoutManager = gridLayoutManager
    }

    override fun onDrinkLoadSuccess(DrinksList: List<Drinks>?) {
        val adapter = PizzasAdapter(this, DrinksList!!, cartLoadListener)
        recycler_drink.adapter = adapter
    }

    override fun onDrinkLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onLoadCartSuccess(cartModelList: List<Cart>) {
        var cartSum = 0
        for(cart in cartModelList!!) cartSum+= cart!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {

    }
}