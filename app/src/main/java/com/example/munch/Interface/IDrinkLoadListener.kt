package com.example.munch.Interface

import com.example.munch.Domain.Drinks

interface IDrinkLoadListener {
    fun onDrinkLoadSuccess(DrinksList: List<Drinks>?)
    fun onDrinkLoadFailed(message:String?)
}