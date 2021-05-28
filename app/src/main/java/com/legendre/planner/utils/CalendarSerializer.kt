package com.legendre.planner.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*

class CalendarSerializer : KSerializer<Calendar> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Calendar", PrimitiveKind.STRING)
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun deserialize(decoder: Decoder): Calendar {
        val calendar = Calendar.getInstance()
        val string = decoder.decodeString()

        calendar.time = simpleDateFormat.parse(string)!!

        return calendar
    }

    override fun serialize(encoder: Encoder, value: Calendar) {
        val string = simpleDateFormat.format(value.time)
        encoder.encodeString(string)
    }
}