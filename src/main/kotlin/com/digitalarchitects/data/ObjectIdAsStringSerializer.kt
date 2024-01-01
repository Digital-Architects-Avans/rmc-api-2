package com.digitalarchitects.data

import com.digitalarchitects.data.user.User
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.litote.kmongo.Id
import org.litote.kmongo.id.IdGenerator

object ObjectIdAsStringSerializer : KSerializer<Id<User>> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Id<User>", PrimitiveKind.STRING)

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): Id<User> {
        return IdGenerator.defaultGenerator.create(decoder.decodeString()) as Id<User>
    }

    override fun serialize(encoder: Encoder, value: Id<User>) {
        encoder.encodeString(value.toString())
    }
}