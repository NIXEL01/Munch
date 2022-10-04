package com.example.munch

import com.example.munch.Domain.Popular

interface ILoadPopularListener {
    fun onPopularLoadSuccess(popularList: List<Popular>?)
    fun onPopularLoadFailed(message:String?)
}