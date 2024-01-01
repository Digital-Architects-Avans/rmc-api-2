package com.digitalarchitects.data.user

import com.digitalarchitects.data.ObjectIdAsStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

enum class UserType {
    STAFF, CLIENT, OTHER
}

@Serializable
data class User (
    @SerialName("_id") @Serializable(with = ObjectIdAsStringSerializer::class) val objectId: Id<User> = newId(),
    @SerialName("userId") val userId: String = objectId.toString(),
    val email: String,
    val password: String,
    val salt: String,
    val userType: UserType,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val street: String,
    val buildingNumber: String,
    val zipCode: String,
    val city: String
)