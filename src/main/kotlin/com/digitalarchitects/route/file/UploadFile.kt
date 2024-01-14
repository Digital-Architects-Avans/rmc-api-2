package com.digitalarchitects.route.file

import com.digitalarchitects.FirebaseStorageUrl
import com.digitalarchitects.FirebaseStorageUrl.getDownloadUrl
import com.google.firebase.cloud.StorageClient
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


fun Route.uploadFile() {

    val bucket = StorageClient.getInstance().bucket()

    authenticate {
        post("/profileImage") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)

            // Check if userId is null, and handle the situation accordingly

            //upload image from multipart
            val multipart = call.receiveMultipart()
            var urlPath = ""

            try {
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val (fileName, fileBytes) = part.convert()

                        // Include userId in the filename
                        val userIdFileName = "$userId/$fileName"

                        bucket.create("avatar_url/images/$fileName", fileBytes, "image/png")
                        urlPath = FirebaseStorageUrl
                            .basePath getDownloadUrl (userIdFileName)
                    }
                }
                call.respondText(urlPath)
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, "Error while uploading image")
            }
        }
    }
}

fun PartData.FileItem.convert() = run {
    val fileBytes = streamProvider().readBytes()
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    Pair(fileName, fileBytes)
}
