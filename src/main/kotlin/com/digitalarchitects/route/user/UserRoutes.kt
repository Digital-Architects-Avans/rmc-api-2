package com.digitalarchitects.route.user

import com.digitalarchitects.data.requests.UpdateUserRequest
import com.digitalarchitects.data.user.User
import com.digitalarchitects.data.user.UserDataSource
import com.digitalarchitects.data.user.UserType
import com.digitalarchitects.security.hashing.HashingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.lang.reflect.InvocationTargetException

fun Route.userRoutes(
    userDataSource: UserDataSource,
    hashingService: HashingService,
) {
    authenticate {
        get("/users") {
            val users = userDataSource.getUsers()
            call.respond(users)
        }

        get("/users/{id}") {
            val id = call.parameters["id"] ?: kotlin.run {
                call.respondText("Invalid user id", status = HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val user = userDataSource.getUserById(id)

                if (user != null) {
                    call.respond(user)
                } else {
                    call.respondText("User with id: $id not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: InvocationTargetException) {
                call.respondText(e.message ?: "Invalid user id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        put("/users/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respondText("Invalid user id", status = HttpStatusCode.BadRequest)
                return@put
            }

            try {
                val updateUserRequest = call.receive<UpdateUserRequest>()

                val saltedHash = hashingService.generateSaltedHash(updateUserRequest.password)
                val user = User(
                    email = updateUserRequest.email,
                    password = saltedHash.hash,
                    salt = saltedHash.salt,
                    userType = UserType.CLIENT,
                    firstName = updateUserRequest.firstName,
                    lastName = updateUserRequest.lastName,
                    phone = updateUserRequest.phone,
                    street = updateUserRequest.street,
                    buildingNumber = updateUserRequest.buildingNumber,
                    zipCode = updateUserRequest.zipCode,
                    city = updateUserRequest.city,
                    profileImageSrc = updateUserRequest.profileImageSrc
                )

                val updated = userDataSource.updateUser(id, user)

                if (updated) {
                    call.respondText("User updated successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("User not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: InvocationTargetException) {
                call.respondText(e.message ?: "Invalid user id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        put("/users/{id}/{profileImageSrc}") {
            val id = call.parameters["id"] ?: run {
                call.respondText("Invalid user id", status = HttpStatusCode.BadRequest)
                return@put
            }
            val profileImageSrc = call.parameters["profileImageSrc"] ?: run {
                call.respondText("Invalid profileImageSrc", status = HttpStatusCode.BadRequest)
                return@put
            }

            try {

                val updated = userDataSource.updateProfileImageSrc(id, profileImageSrc)

                if (updated) {
                    call.respondText("User profileImageSrc updated successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("User not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: InvocationTargetException) {
                call.respondText(e.message ?: "Invalid user id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        get("/users/email/{email}") {
            val email = call.parameters["email"] ?: run {
                call.respondText("Invalid email", status = HttpStatusCode.BadRequest)
                return@get
            }

            try {
                val user = userDataSource.getUserByEmail(email)

                if (user != null) {
                    call.respond(user)
                } else {
                    call.respondText("User not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: Exception) {
                call.respondText("An error occurred: $e", status = HttpStatusCode.InternalServerError)
            }
        }

        delete("/users/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                return@delete
            }

            try {
                val deleted = userDataSource.deleteUserById(id)
                if (deleted) {
                    call.respondText("User deleted successfully", status = HttpStatusCode.OK)
                } else {
                    call.respondText("User not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: IllegalArgumentException) {
                call.respondText(e.message ?: "Invalid user id format", status = HttpStatusCode.BadRequest)
            } catch (e: InvocationTargetException) {
                call.respondText(e.message ?: "Invalid user id format", status = HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respondText("Unexpected error: $e", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
