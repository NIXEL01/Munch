package com.example.munch.Domain

class Cart {
        var key: String?=null
        var popularName: String?=null
        var popularImage: String?=null
        var popularPrice: String?=null
        var quantity = 0
        var totalPrice = 0
}


/* data class Cart(var popularImage:String,
                   var popularName:String,
                   var popularPrice:String,
                   var key:String?=null
)
{
        constructor():this(
                "",
                "",
                "",
                "",

        )
        var quantity = 0
        var totalPrice = 0f
}*/