package com.example.munch.Adapter

import android.content.Context
import android.text.Layout
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.munch.Activity.DrinksActivity
import com.example.munch.Activity.MainActivity
import com.example.munch.Activity.PizzasActivity
import com.example.munch.Domain.Cart
import com.example.munch.Domain.Drinks
import com.example.munch.Domain.Popular
import com.example.munch.Interface.ICartLoadListener
import com.example.munch.Interface.IRecyclerClickListener
import com.example.munch.R
import com.example.munch.UpdateCartEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DrinksAdapter(private val context: DrinksActivity,
                    private val DrinksList: List<Drinks>,
                    private val cartListener: ICartLoadListener)
    : RecyclerView.Adapter<DrinksAdapter.DrinksViewholder>(){




    class DrinksViewholder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
                            val drinkImageView: ImageView = itemView.findViewById(R.id.drink_imageView)
                            val drinkNameTv: TextView = itemView.findViewById(R.id.drink_tv)
                            val drinkPriceTv: TextView = itemView.findViewById(R.id.drink_price)

        private var clickListener: IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener) {
            this.clickListener = clickListener;
        }
        init {
            itemView.setOnClickListener (this)
        }

        override fun onClick(p0: View?) {
            clickListener!!.onItemClickListener(p0, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinksViewholder {
        val view = LayoutInflater.from (parent.context)
            .inflate(R.layout.viewholder_drinks, parent, false)
        return DrinksViewholder(view)
    }

    override fun onBindViewHolder(holder: DrinksViewholder, position: Int) {
        val drinks = DrinksList [position]
        holder.drinkNameTv.text = drinks.name
        holder.drinkPriceTv.text = StringBuilder("₦").append(DrinksList[position].price)
        Glide.with(context)
            .load(drinks.image)
            .into(holder.drinkImageView)

        holder.setClickListener(object:IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(DrinksList[position])
            }

        })

    }

    private fun addToCart(drinks: Drinks) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(drinks.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val cartModel = snapshot.getValue(Cart::class.java)
                        val updateData: MutableMap<String, Any> = HashMap()
                        cartModel!!.quantity = cartModel!!.quantity + 1;
                        updateData["quantity"] = cartModel!!.quantity
                        updateData["totalPrice"] =
                            cartModel!!.quantity * cartModel.popularPrice!!.toFloat()

                        userCart.child(drinks.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                org.greenrobot.eventbus.EventBus.getDefault().postSticky(
                                    UpdateCartEvent()
                                )
                              //  cartListener.onLoadCartFailed("Successfully added to cart")
                            }
                            .addOnFailureListener{e -> cartListener.onLoadCartFailed(e.message.toString()) }
                    }else{
                        val cartModel = Cart()
                        cartModel.key = drinks.key
                        cartModel.popularName = drinks.name
                        cartModel.popularImage = drinks.image
                        cartModel.popularPrice = drinks.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = drinks.price!!.toInt()

                        userCart.child(drinks.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                org.greenrobot.eventbus.EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Successfully added to cart")
                            }
                            .addOnFailureListener{e -> cartListener.onLoadCartFailed(e.message!!)}
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }

            })
    }

    override fun getItemCount(): Int {
        return DrinksList.size
    }
}