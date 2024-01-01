package com.digitalarchitects

import com.digitalarchitects.data.user.MongoUserDataSource
import com.digitalarchitects.data.vehicle.MongoVehicleDataSource
import com.digitalarchitects.plugins.*
import com.digitalarchitects.security.hashing.SHA256HashingService
import com.digitalarchitects.security.token.JwtTokenService
import com.digitalarchitects.security.token.TokenConfig
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            val db = KMongo.createClient(
                connectionString = "mongodb+srv://rmcKtor:rmcKtor@cluster0.j39xyce.mongodb.net/rmcKtor?retryWrites=true&w=majority"
            ).coroutine
                .getDatabase("rmcKtor")
            val userDataSource = MongoUserDataSource(db)
            val vehicleDataSource = MongoVehicleDataSource(db)
            val tokenService = JwtTokenService()
            val tokenConfig = TokenConfig(
                issuer = environment.config.property("jwt.issuer").getString(),
                audience = environment.config.property("jwt.audience").getString(),
                expiresIn = 365L * 1000L * 60 * 60 * 24, // One year in milliseconds
                secret = environment.config.property("jwt.secret").getString()

            )
            val hashingService = SHA256HashingService()

            configureSecurity(tokenConfig)
            configureRouting(userDataSource, vehicleDataSource, hashingService, tokenService, tokenConfig)
            configureMonitoring()
            configureSerialization()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
