package com.digitalarchitects.plugins

import com.digitalarchitects.data.rental.RentalDataSource
import com.digitalarchitects.data.user.UserDataSource
import com.digitalarchitects.data.vehicle.VehicleDataSource
import com.digitalarchitects.route.auth.authRoutes
import com.digitalarchitects.route.file.uploadFile
import com.digitalarchitects.route.rental.rentalRoutes
import com.digitalarchitects.route.user.userRoutes
import com.digitalarchitects.route.vehicle.vehicleRoutes
import com.digitalarchitects.security.hashing.HashingService
import com.digitalarchitects.security.token.TokenConfig
import com.digitalarchitects.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    vehicleDataSource: VehicleDataSource,
    rentalDataSource: RentalDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        authRoutes(userDataSource, hashingService, tokenService, tokenConfig)
        userRoutes(userDataSource)
        vehicleRoutes(userDataSource, vehicleDataSource)
        rentalRoutes(userDataSource, rentalDataSource, vehicleDataSource)
        uploadFile()
    }
}
