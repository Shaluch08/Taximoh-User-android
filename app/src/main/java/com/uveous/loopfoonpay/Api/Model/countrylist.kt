package com.uveous.loopfoonpay.Api.Model

import com.google.gson.annotations.SerializedName

data class countrylist(

    @SerializedName("status")
    var status:Int,

    @SerializedName("result")
    var result :ArrayList<result>


)

  class result {

      @SerializedName("id")
      var id:Int ?=0
      @SerializedName("country_name")
      var country_name:String ?=""
    @SerializedName("country_code")
      var country_code:String ?=""
  @SerializedName("phone_code")
      var phone_code:String ?=""
      @SerializedName("icon")
      var icon:String ?=""

}
