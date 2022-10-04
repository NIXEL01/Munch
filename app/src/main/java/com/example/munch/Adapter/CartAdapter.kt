package com.example.munch.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.munch.Domain.Cart
import com.example.munch.R
import com.example.munch.UpdateCartEvent
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus

class CartAdapter (
    private val context: Context,
    private val cartModelList:List<Cart>
    ): RecyclerView.Adapter<CartAdapter.MyCartViewHolder>() {
        class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var btnMinus: ImageView? = null
            var btnPlus: ImageView? = null
            var btnDelete: ImageView? = null
            var imageView: ImageView? = null
            var txtName: TextView? = null
            var txtPrice: TextView? = null
            var txtQuantity: TextView? = null


            init {
                btnMinus = itemView.findViewById(R.id.btnMinus) as ImageView
                btnPlus = itemView.findViewById(R.id.btnPlus) as ImageView
                btnDelete = itemView.findViewById(R.id.btnDelete) as ImageView
                imageView = itemView.findViewById(R.id.drink_imageView) as ImageView
                txtName = itemView.findViewById(R.id.drink_tv) as TextView
                txtPrice = itemView.findViewById(R.id.drink_price) as TextView
                txtQuantity = itemView.findViewById(R.id.txtQuantity) as TextView
            }
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_cart, parent, false)
        return MyCartViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(cartModelList[position].popularImage)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(cartModelList[position].popularName)
        holder.txtPrice!!.text = StringBuilder("₦").append(cartModelList[position].popularPrice)
        holder.txtQuantity!!.text = StringBuilder("").append(cartModelList[position].quantity)

        holder.btnMinus!!.setOnClickListener {_ -> minusCartItem (holder,cartModelList[position])}
        holder.btnPlus!!.setOnClickListener {_ -> plusCartItem (holder,cartModelList[position])}
        holder.btnDelete!!.setOnClickListener {_ ->
            val dialog = AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Do you really want to delete this item?")
                .setNegativeButton("CANCEL"){dialog,_ -> dialog.dismiss()}
                .setPositiveButton("DELETE") {dialog,_->

                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance()
                        .getReference("Cart")
                        .child("UNIQUE_USER_ID")
                        .child(cartModelList[position].key!!)
                        .removeValue()
                        .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }
                }
                .create()
            dialog.show()
        }
    }

    private fun plusCartItem(holder: MyCartViewHolder, cart: Cart) {
        cart.quantity += 1
        cart.totalPrice = cart.quantity * cart.popularPrice!!.toInt()
        holder.txtQuantity!!.text = StringBuilder("").append(cart.quantity)
        updateFirebase(cart)
    }

    private fun minusCartItem(holder: MyCartViewHolder, cart: Cart) {
        if (cart.quantity > 1)
        {
            cart.quantity -= 1
            cart.totalPrice = cart.quantity * cart.popularPrice!!.toInt()
            holder.txtQuantity!!.text = StringBuilder("").append(cart.quantity)
            updateFirebase(cart)
        }
    }

    private fun updateFirebase(cart: Cart) {
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .child(cart.key!!)
            .setValue(cart)
            .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }
    }


    override fun getItemCount(): Int {
        return(cartModelList.size)
    }

}
