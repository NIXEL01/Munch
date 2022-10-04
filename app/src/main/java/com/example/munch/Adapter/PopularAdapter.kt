package com.example.munch.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.munch.Activity.MainActivity
import com.example.munch.Adapter.PopularAdapter.PopularViewHolder
import com.example.munch.Domain.Cart
import com.example.munch.Domain.Popular
import com.example.munch.Interface.ICartLoadListener
import com.example.munch.Interface.IRecyclerClickListener
import com.example.munch.R
import com.example.munch.UpdateCartEvent
import com.google.common.eventbus.EventBus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.grpc.Context.key
import org.greenrobot.eventbus.EventBus.getDefault
import java.util.Locale.getDefault
import kotlin.Unit.toString

class PopularAdapter(private val context: MainActivity,
                     private val popularList: List<Popular>,
                     private val cartListener: ICartLoadListener) :
    RecyclerView.Adapter<PopularViewHolder>() {


    class PopularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val popularImageView: ImageView = itemView.findViewById(R.id.popularPic)
        val popularNameTv: TextView = itemView.findViewById(R.id.popularText)
        val popularPriceTv: TextView = itemView.findViewById(R.id.price)
        val addBtnView : ImageView = itemView.findViewById(R.id.addBtn)

        private var clickListener:IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener)
        {
            this.clickListener = clickListener;
        }

        init {
            addBtnView.setOnClickListener (this)
        }

        override fun onClick(p0: View?) {
            clickListener!!.onItemClickListener(p0, adapterPosition)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_popular, parent, false)
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val popular = popularList[position]
        holder.popularNameTv.text = popular.popularName
        holder.popularPriceTv.text = StringBuilder("₦").append(popularList[position].popularPrice)
        Glide.with(context)
            .load(popular.popularImage)
            .into(holder.popularImageView)

        holder.setClickListener(object:IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(popularList[position])
            }

        })

    }

    private fun addToCart(popular: Popular) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(popular.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val cartModel = snapshot.getValue(Cart::class.java)
                        val updateData : MutableMap<String, Any> = HashMap()
                        cartModel!!.quantity = cartModel!!.quantity + 1;
                        updateData["quantity"] = cartModel!!.quantity
                        updateData["totalPrice"] =
                            cartModel!!.quantity * cartModel.popularPrice!!.toFloat()

                        userCart.child(popular.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                org.greenrobot.eventbus.EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Successfully added to cart")
                            }
                            .addOnFailureListener{e -> cartListener.onLoadCartFailed(e.message.toString()) }
                    }else{
                        val cartModel = Cart()
                        cartModel.key = popular.key
                        cartModel.popularName = popular.popularName
                        cartModel.popularImage = popular.popularImage
                        cartModel.popularPrice = popular.popularPrice
                        cartModel.quantity = 1
                        cartModel.totalPrice = popular.popularPrice!!.toInt()

                        userCart.child(popular.key!!)
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
                return popularList.size
            }

}
