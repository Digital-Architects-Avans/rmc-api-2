package com.digitalarchitects

import com.digitalarchitects.data.requests.AuthRequest
import com.digitalarchitects.data.responses.AuthResponse
import com.digitalarchitects.data.user.User
import com.digitalarchitects.data.user.UserDataSource
import com.digitalarchitects.data.user.UserType
import com.digitalarchitects.security.hashing.HashingService
import com.digitalarchitects.security.hashing.SaltedHash
import com.digitalarchitects.security.token.TokenClaim
import com.digitalarchitects.security.token.TokenConfig
import com.digitalarchitects.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("/signup") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.email.isBlank() || request.password.isBlank()
        val isPasswordTooShort = request.password.length < 8
        if (areFieldsBlank || isPasswordTooShort) {
            call.respond(HttpStatusCode.Conflict, "Invalid username or password")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt,
            userType = UserType.CLIENT,
            firstName = request.firstName,
            lastName = request.lastName,
            phone = request.phone,
            street = request.street,
            buildingNumber = request.buildingNumber,
            zipCode = request.zipCode,
            city = request.city
        )
        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return@post
        }

        call.respond(HttpStatusCode.Created)
    }
}

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/signin") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound, "User not found")
            return@post
        }

        val isValidPassword = hashingService.verifySaltedHash(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

// This route will check if the token the user has saved in preferences is still valid
// You can make a request to this route during the splash screen of the app
// If not valid redirect user to login screen
fun Route.authenticate() {
    authenticate {
        get("/authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("/secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}