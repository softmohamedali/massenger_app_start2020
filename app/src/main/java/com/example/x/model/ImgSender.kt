package com.example.x.model

import java.util.*

data  class ImgSender(override var mas: String,
                      override var senderId: String,
                      override var recipienId: String,
                      override var senderName: kotlin.String,
                      override var recipientNamr: String,
                      override var data: Date,
                      override var type: String=MassageType.IMG):MSG {
    constructor():this("","","","","",Date())
}