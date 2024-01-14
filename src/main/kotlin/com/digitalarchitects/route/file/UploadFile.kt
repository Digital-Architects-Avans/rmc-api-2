package com.digitalarchitects.route.file

import com.digitalarchitects.data.responses.AuthResponse
import com.digitalarchitects.data.responses.ProfileImageResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.uploadFile() {
    authenticate {
        post("/profileImage") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)

            // Create a directory if it doesn't exist
            val uploadDir = File("build/resources/main/static/profileImages")
            if (!uploadDir.exists()) {
                uploadDir.mkdirs()
            }

            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> Unit
                    is PartData.FileItem -> {
                        if (part.name == "image") {
                            val fileName = "$userId.jpg"
                            val filePath = "build/resources/main/static/profileImages/$fileName"
                            part.streamProvider().use { input ->
                                File(filePath).writeBytes(input.readBytes())
                            }
                        }
                    }

                    else -> Unit
                }
            }
            call.respond(HttpStatusCode.OK)
        }

        get("/profileImageUrl/{userId}") {
            val userId = call.parameters["userId"] ?: run {
                call.respondText("Invalid userId", status = HttpStatusCode.BadRequest)
                return@get
            }

            val imageFile = File("build/resources/main/static/profileImages/$userId.jpg")

            if (imageFile.exists()) {
                // Construct the full URL including host, port, and protocol
                val fullUrl = call.request.origin.run {
                    URLBuilder(
                        protocol = URLProtocol.createOrDefault(scheme), // Use URLProtocol.createOrDefault
                        host = localHost,
                        port = localPort
                    )
                }
                val imageUrl = "$fullUrl/profileImages/$userId.jpg"

                call.respond(
                    status = HttpStatusCode.OK,
                    message = ProfileImageResponse(
                        profileImageSrc = imageUrl
                    )
                )
            } else {
                call.respondText("Image not found for userId: $userId", status = HttpStatusCode.NotFound)
            }
        }
    }

    get("/profileImages/{userId}") {
        val userId = call.parameters["userId"] ?: run {
            call.respondText("Invalid userId", status = HttpStatusCode.BadRequest)
            return@get
        }

        val imageFile = File("build/resources/main/static/profileImages/$userId.jpg")
        if (imageFile.exists()) {
            call.respond(LocalFileContent(imageFile))
        } else {
            call.respond(HttpStatusCode.NotFound, "Image not found for userId: $userId")
        }
    }
}
