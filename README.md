![rmc-logo](https://github.com/Digital-Architects-Avans/rmc-api/blob/master/src/main/resources/images/rmc-logo.png)

# Rent My Car - REST API

A Kotlin REST API with the Ktor & KMongdoDB frameworks

## Table of Contents

- [Introduction](https://github.com/Digital-Architects-Avans/rmc-api/blob/master/README.md#introduction)
- [Getting Started](https://github.com/Digital-Architects-Avans/rmc-api/blob/master/README.md#getting-started)
- [Usage](https://github.com/Digital-Architects-Avans/rmc-api/blob/master/README.md#usage)
- [Endpoints](https://github.com/Digital-Architects-Avans/rmc-api/blob/master/README.md#endpoints)
- [Contributing](https://github.com/Digital-Architects-Avans/rmc-api/blob/master/README.md#contributing)
- [License](https://github.com/Digital-Architects-Avans/rmc-api/blob/master/README.md#license)

## Introduction

Our project is a modern and user-friendly mobile application built using the Ktor framework with the KMongoDB database for seamless data persistence. Designed to serve the needs of people seeking quick and hassle-free car rental services, as well as those who wish to make their vehicles available for rent, this Android app employs the power of Jetpack Compose for an elegant and intuitive user interface.

## Getting Started

A brief set of instructions on how to set up and run the project locally:

1. GitHub RMC API project repository:
- Visit the GitHub repository at https://github.com/Digital-Architects-Avans/rmc-api-2.

2. Download and Unzip the Project:
- Download the project repository and unzip the folder to your local machine.

3. Open the Project in IntelliJ IDEA:
- Launch IntelliJ IDEA by JetBrains.
- Open the unzipped "rmc-api-2-master" folder as a project.

4. Start the Application:
- Ensure that your development environment is set up and all dependencies are loaded.
- Start the application by running the main() function located in the "Application.kt" file.

6. Explore Predefined HTTP Requests:
- Once the application is running, you can explore predefined HTTP requests for various entity types, including Rental, User, and Vehicle.
- Find these requests in the route map, organized within subdirectories for each entity type.
- Look for the corresponding "<Type>Requests.http" file in the respective subdirectory for examples of predefined HTTP requests.

## Usage

Below is an example of a usecase that employs some of the endpoints provided by this web API:

1. **Endpoint 1**
   - Description: This endpoint registers a new account for a user.
   - HTTP Method: POST
   - URL: `/signup`
   - Request Example:
     ```http
        POST http://localhost:8080/user/signup
        Content-Type: application/json
      {
          "email": "owner1@example.com",
           "password": "password",
           "userType": "CLIENT",
           "firstName": "Emma",
           "lastName": "de Jong",
           "phone": "0642748593",
           "street": "Rietsemalaan",
           "buildingNumber": "14",
           "zipCode": "4817 KX",
           "city": "Breda"
      }
     ```
   - Response Example:
     ```json
      {"id":1,"email":"owner3@email.com","userType":"CLIENT","firstName":"","lastName":"","phone":"","street":"","buildingNumber":"","zipCode":"","city":"}
     ```

2. **Endpoint 2**
   - Description: Owner of a vehicle creates a vehicle.
   - HTTP Method: POST
   - URL: `/vehicles`
   - Request Example:
     ```http
     POST http://localhost:8080/vehicles
      Content-Type: application/json
      Authorization: Bearer $token
     {
     "userId": "FILL_USER",
     "brand": "Ford",
     "model": "F-150 Raptor",
     "year": 2023,
     "vehicleClass": "MPV",
     "engineType": "ICE",
     "licensePlate": "V-512-XD",
     "imgLink": 1,
     "latitude": 51.587839,
     "longitude": 4.755652,
     "price": 80,
     "availability": true
      }
     ```
   - Response Example:
     ```json
     {
     "_id": "659da9aa61042f30d93c9cab", "vehicleId": "659da9aa61042f30d93c9cab", "userId": "659da373fb940c279b376fb5", "brand": "Ford", "model": "F-150 Raptor", "year": 2023, "vehicleClass": "MPV", "engineType": "ICE", "licensePlate": "V-512-XE", "imgLink": 1, "latitude": 51.587837, "longitude": 4.755652, "price": 80.0, "availability": true
}
     ```

3. **Endpoint 3**
   - Description: This endpoint registers a rental for a specific vehicle.
   - HTTP Method: POST
   - URL: `/rental`
   - Request Example:
     ```http
      POST http://localhost:8080/rental
      Content-Type: application/json
      Authorization: Bearer $token
      
      {
        "vehicleId": "TEST UPDATE",
        "userId": "FILL_RENTER_3",
        "date": "2023-03-08",
        "price": 80.0,
        "latitude": 51.587839,
        "longitude": 4.755652,
        "status": "CANCELLED",
        "distanceTravelled": 0,
        "score": 0
      }
     ```
   - Response Example:
     ```json
      {"id":1,"vehicleId":1,"userId":1,"date":"2023-10-21","price":250.0,"latitude":51.571915,"longitude":4.768323,"status":"PENDING","distanceTravelled":0.0,"score":100}
     ```

4. **Endpoint 4**
   - Description: This endpoint allows a vehicle owner to deny or approve a rental request.
   - HTTP Method: GET
   - URL: `/rentals/rentalId/APPROVED`
   - Request Example:
     ```http
      GET http://localhost:8080/rental/status/1?status=approved
      Accept: application/json
      Authorization: Bearer $token
     ```
   - Response Example:
     ```json
      {"id":1,"vehicleId":1,"userId":1,"date":"2023-10-21","price":250.0,"latitude":51.571915,"longitude":4.768323,"status":"APPROVED","distanceTravelled":0.0,"score":100}
     ```

There are 24 endpoints in total which allows you to perform a various CRUD operations on the User, Vehicle and Rental entities. These endpoints collectively support the functionalities required for users to register, list vehicles, make rental reservations, and manage those reservations, catering to the needs of both vehicle owners and renters in a vehicle rental platform.


## Endpoints

Endpoint List
In the "route" directory, you can find routes and endpoints organized per entity as follows:

### Endpoint List

In the "route" directory, you can find routes and endpoints organized by routing as follows:

#### Auth Endpoints (AuthRoutes.kt)

| Endpoint              | Description                 | Endpoint No. | Access  |
|-----------------------|-----------------------------|--------------|---------|
| `post("/signup")`     | Register an account         | [1]          | CLIENT  |
| `post("/signin")`     | Log in                      | [2]          | CLIENT  |
| `post("/refreshToken")` | Refreshed JWT Token       | [3]          | CLIENT  |
| `get("/authenticate")`| Authenticate a user by JWT  | [4]          | STAFF   |
| `get("/secret")`      | Get UserId from JWT         | [5]          | STAFF   |

#### User Endpoints (UserRoutes.kt)

| Endpoint              | Description                 | Endpoint No. | Access  |
|-----------------------|-----------------------------|--------------|---------|
| `get("/users")`       | Retrieve all users          | [6]          | STAFF   |
| `get("/users/{id}")`  | Retrieve a specific user    | [7]          | STAFF   |
| `put("/users/{id}")`  | Edit a specific user        | [8]          | STAFF   |
| `get("/users/email/{email}")`  | Get user by email        | [9]          | STAFF   |
| `delete("/{id}")`     | Delete a specific user      | [10]          | STAFF   |


#### Vehicle Endpoints (VehicleRoutes.kt)

| Endpoint                     | Description                 | Endpoint No. | Access  |
|------------------------------|-----------------------------|--------------|---------|
| `post("/vehicles")`          | Register a vehicle          | [11]         | CLIENT  |
| `get("/vehicles")`           | Retrieve a specific vehicle | [12]         | CLIENT  |
| `get("/vehicles/{id}")`      | Retrieve a specific vehicle by ID       | [13]         | STAFF   |
| `get("/vehicles/licenseplate/{licencseplate}")`       | Retrieve vehicle by license plate | [14]         | CLIENT  |
| `put("/vehicles/{id}")`      | Edit a specific vehicle     | [15]         | CLIENT  |
| `delete("/vehicles/{id}")`   | Delete a specific vehicle   | [16]         | CLIENT  |

#### Rental Endpoints (RentalRoutes.kt)

| Endpoint                             | Description                 | Endpoint No. | Access  |
|--------------------------------------|-----------------------------|--------------|---------|
| `get("/rentals")`                   | Retrieve own rentals        | [17]         | CLIENT  |
| `get("/rentals/{id}")`              | Retrieve a specific rental   | [18]         | CLIENT  |
| `get("/rentals/user/{userId})`      | Retrieve rentals of specific user | [19]     | CLIENT  |
| `get("/rentals/vehicle/{vehicleId})`| Retrieve rentals of specific vehicle    | [20]         | CLIENT  |
| `post("/rentals")`                  | Create a rentals             | [21]         | STAFF   |
| `put("/rentals/{rentalId}           | Edit a specific rental       | [22]         | CLIENT  |
| `get("rental/{rentalId/{status}"`   | Edit a specific rental status | [23]         | STAFF   |
| `delete("/rentals/{id}")`           | Delete a specific rental     | [24]         | CLIENT  |



## Contributing

### How to Contribute

We welcome contributions from the community to help improve our project. You can contribute in various ways:

- **Bug Reports**: If you come across any issues or bugs, please report them on our [GitHub repository](https://github.com/Digital-Architects-Avans/rmc-api-2). Be sure to provide detailed information about the problem, including steps to reproduce it.

- **Feature Requests**: If you have ideas for new features or enhancements, we'd love to hear them. Open an issue on GitHub with a clear description of the feature you're suggesting.

- **Code Contributions**: If you're interested in contributing code, feel free to fork our repository and submit pull requests. We encourage you to follow our coding style and guidelines.

Additionally, you can reach out to our team by opening a discussion on our GitHub repository for any questions or collaboration opportunities.

We appreciate your support and look forward to working together to make our project even better.

## License

This project is released under the [Apache License 2.0](LICENSE).

### Terms of Use and Distribution

By using this project, you agree to be bound by the terms and conditions of the Apache License 2.0. You may use, modify, distribute, and contribute to this project, subject to the terms specified in the license. Please review the [LICENSE](LICENSE) file for the full text of the Apache License 2.0 and understand its implications. If you have any questions or concerns, please contact the project owner for clarification.

Your use and contributions to this project are greatly appreciated, and we encourage collaboration in accordance with the terms of the Apache License 2.0.

