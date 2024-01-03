package com.digitalarchitects.data.user

import com.digitalarchitects.data.requests.UpdateUserRequest
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId

class MongoUserDataSource(
    db: CoroutineDatabase
): UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUsers(): List<User> {
        return users.find().toList()
    }

    override suspend fun getUserById(userId: String): User? {
        return users.findOne(User::userId eq userId)
    }

    override suspend fun updateUser(userId: String, updatedUser: UpdateUserRequest): Boolean {
        val user = getUserById(userId) ?: return false

        val updatedDocument = user.copy(
            password = updatedUser.password,
            userType = updatedUser.userType,
            firstName = updatedUser.firstName,
            lastName = updatedUser.lastName,
            phone = updatedUser.phone,
            street = updatedUser.street,
            buildingNumber = updatedUser.buildingNumber,
            zipCode = updatedUser.zipCode,
            city = updatedUser.city
        )

        val updateResult = users.replaceOne(User::userId eq userId, updatedDocument)

        return updateResult.wasAcknowledged()
    }

    override suspend fun deleteUserById(userId: String): Boolean {
        val userIdAsId: Id<User> = ObjectId(userId).toId()
        return users.deleteOneById(userIdAsId).wasAcknowledged()
    }
}