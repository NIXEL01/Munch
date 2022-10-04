package com.example.munch.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.Transliterator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.munch.Adapter.CartAdapter
import com.example.munch.Domain.Cart
import com.example.munch.Domain.Popular
import com.example.munch.Interface.ICartLoadListener
import com.example.munch.R
import com.example.munch.UpdateCartEvent
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.viewholder_categories.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : AppCompatActivity(), ICartLoadListener {

     var cartLoadListener: ICartLoadListener? = null

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
        loadCartFromFirebase()
    }

    private lateinit var cartModelList: List<Cart>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)



        init()
        loadCartFromFirebase()
    }

    private fun loadCartFromFirebase() {
        val cartModels : MutableList<Cart> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children) {
                        val cartModel = cartSnapshot.getValue(Cart::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }


            })
    }

    private fun init() {
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        cart_Rv!!.layoutManager = layoutManager
        cart_Rv!!.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
       // btnBack!!.setOnClickListener { finish() }

    }


    override fun onLoadCartSuccess(cartModelList: List<Cart>) {
        var sum = 0.0
        for (cartModel in cartModelList!!){
             sum+= cartModel!!.totalPrice

            btn_Checkout.setOnClickListener {
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("totalPrice" , cartModel.totalPrice)
                startActivity(intent)
            }
        }
        txtTotal.text = StringBuilder("₦").append(sum)

        val adapter = CartAdapter(this,cartModelList)
        cart_Rv!!.adapter = adapter

    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }
}