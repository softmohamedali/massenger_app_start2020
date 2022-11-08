package com.example.x.model

import java.util.*

data class Massage(override var mas:String,
                   override var senderId:String,
                   override var recipienId:String,
                   override var senderName: String,
                   override var recipientNamr:String,
                   override var data:Date,
                   override var type: String=MassageType.Text):MSG {

    constructor():this("","","","","",Date())

}