package com.example.munch.Domain

/*class Popular {
    var key: String? = null
    var popularName: String? = null
    var popularImage: String? = null
    var popularPrice: String? = null
}*/

data class Popular(val popularImage:String,
                   val popularName:String,
                   val popularPrice:String,
                   var key:String?=null)
{
    constructor():this(
        "",
        "",
        "",
        "",
    )

}
