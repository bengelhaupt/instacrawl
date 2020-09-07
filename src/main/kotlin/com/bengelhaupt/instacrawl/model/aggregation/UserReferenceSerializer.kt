package com.bengelhaupt.instacrawl.model.aggregation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UserReferenceSerializer : KSerializer<UserReference> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UserReference", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UserReference) {
        encoder.encodeString(
            "${value.id}/${value.username}"
        )
    }

    override fun deserialize(decoder: Decoder): UserReference {
        val (id, username) = decoder.decodeString().split("/")
        return UserReference(
            id, username
        )
    }
}
