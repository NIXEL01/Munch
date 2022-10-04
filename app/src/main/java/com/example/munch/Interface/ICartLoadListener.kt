package com.example.munch.Interface

import com.example.munch.Domain.Cart
import com.example.munch.Domain.Popular

interface ICartLoadListener {
    fun onLoadCartSuccess(cartModelList: List<Cart>)
    fun onLoadCartFailed(message:String?)
}