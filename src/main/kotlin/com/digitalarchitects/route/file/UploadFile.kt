package com.digitalarchitects.route.file

import com.digitalarchitects.FirebaseStorageUrl
import com.digitalarchitects.FirebaseStorageUrl.getDownloadUrl
import com.digitalarchitects.data.responses.AuthResponse
import com.digitalarchitects.data.responses.ProfileImageResponse
import com.google.cloud.storage.BlobId
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
import java.util.concurrent.TimeUnit


fun Route.uploadFile() {

    val bucket = StorageClient.getInstance().bucket()
    val storage = bucket.storage

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
                        val fileName = "profile_images/$userId"
                        val fileBytes = part.streamProvider().readBytes()

                        bucket.create(fileName, fileBytes, "image/png")
                        urlPath = FirebaseStorageUrl
                            .basePath getDownloadUrl (fileName)

                        // Generate a download URL with the token
                        val downloadUrl = storage.get(BlobId.of(bucket.name, fileName)).signUrl(365, TimeUnit.DAYS)
                        urlPath = downloadUrl.toString()
                    }
                }
                call.respond(
                    status = HttpStatusCode.OK,
                    message = ProfileImageResponse(
                        profileImageSrc = urlPath
                    )
                )
                println("Image uploaded successfully: $urlPath")
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
