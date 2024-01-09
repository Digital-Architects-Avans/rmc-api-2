![rmc-logo](src/main/resources/images/rmc-logo.png)

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

Our project is a modern and user-friendly mobile application built using the Ktor framework with the Exposed DAO API for seamless data persistence. Designed to serve the needs of people seeking quick and hassle-free car rental services, as well as those who wish to make their vehicles available for rent, this Android app employs the power of Jetpack Compose for an elegant and intuitive user interface.

## Getting Started

A brief set of instructions on how to set up and run the project locally:

1. GitHub RMC API project repository:
- Visit the GitHub repository at https://github.com/Digital-Architects-Avans/rmc-api.

2. Download and Unzip the Project:
- Download the project repository and unzip the folder to your local machine.

3. Open the Project in IntelliJ IDEA:
- Launch IntelliJ IDEA by JetBrains.
- Open the unzipped "rmc-api-master" folder as a project.

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
   - URL: `/user/signup`
   - Request Example:
     ```http
        POST http://localhost:8080/user/signup
        Content-Type: application/json
      {
        "email": "owner1@email.com",
        "userType": "CLIENT",
        "password": "StrongPassword1!"
      }
     ```
   - Response Example:
     ```json
      {"id":1,"email":"owner3@email.com","userType":"CLIENT","firstName":"","lastName":"","phone":"","street":"","buildingNumber":"","zipCode":"","city":"}
     ```

2. **Endpoint 2**
   - Description: Owner of a vehicle creates a vehicle.
   - HTTP Method: POST
   - URL: `/vehicle/createVehicle`
   - Request Example:
     ```http
     POST http://localhost:8080/vehicle/createVehicle
      Content-Type: application/json
      Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hlbGxvIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4MC8iLCJlbWFpbCI6Im93bmVyMUBlbWFpbC5jb20iLCJ1c2VyVHlwZSI6IkNMSUVOVCIsInVzZXJJZCI6MSwiZXhwIjoxNjk3OTAwNjA0fQ.tpFfUZkOtO5vcNmGQrTx24A642A1PB7bQt-IDTKsARQ
     {
     "brand": "Tesla",
     "model": "Model S",
     "year": 2023,
     "vehicleClass": "Sedan",
     "engineType": "BEV",
     "licensePlate": "4-ALF-55",
     "imgLink": "https://www.google.com",
     "latitude": 51.571915,
     "longitude": 4.768323,
     "price": 250.00,
     "availability": true
      }
     ```
   - Response Example:
     ```json
      {"id":1,"userId":1,"brand":"Tesla","model":"Model S","year":2023,"vehicleClass":"Sedan","engineType":"BEV","licensePlate":"4-ALF-55","imgLink":"https://www.google.com","latitude":51.571915,"longitude":4.768323,"price":250.0,"availability":true}
     ```

3. **Endpoint 3**
   - Description: This endpoint registers a rental for a specific vehicle.
   - HTTP Method: POST
   - URL: `/rental/createRental/1`
   - Request Example:
     ```http
      POST http://localhost:8080/rental/createRental/1
      Content-Type: application/json
      Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hlbGxvIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4MC8iLCJlbWFpbCI6InJlbnRlcjFAZW1haWwuY29tIiwidXNlclR5cGUiOiJDTElFTlQiLCJ1c2VySWQiOjMsImV4cCI6MTY5NzkwMTYyOH0.DAO3cOV368frAqGJq7Uil25nlcqR3b66KlHVEqzcfC8
      
      {
        "date": "2023-10-21"
      }
     ```
   - Response Example:
     ```json
      {"id":1,"vehicleId":1,"userId":1,"date":"2023-10-21","price":250.0,"latitude":51.571915,"longitude":4.768323,"status":"PENDING","distanceTravelled":0.0,"score":100}
     ```

4. **Endpoint 4**
   - Description: This endpoint allows a vehicle owner to deny or approve a rental request.
   - HTTP Method: GET
   - URL: `/rental/status/1?status=approved`
   - Request Example:
     ```http
      GET http://localhost:8080/rental/status/1?status=approved
      Accept: application/json
      Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hlbGxvIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4MC8iLCJlbWFpbCI6Im93bmVyMUBlbWFpbC5jb20iLCJ1c2VyVHlwZSI6IkNMSUVOVCIsInVzZXJJZCI6MSwiZXhwIjoxNjk3OTAwOTkyfQ.NLvSgWPq9f7z4qL903HrZ1-wFf38gWkwHxW5u8i8zcI
     ```
   - Response Example:
     ```json
         {
     "id": 1,
     "vehicleId": 1,
     "userId": 1,
     "date": "2023-10-21",
     "price": 250.0,
     "latitude": 51.571915,
     "longitude": 4.768323,
     "status": "APPROVED",
     "distanceTravelled": 0.0,
     "score": 100
      }
     ```

There are 27 endpoints in total which allows you to perform a various CRUD operations on the User, Vehicle and Rental entities. These endpoints collectively support the functionalities required for users to register, list vehicles, make rental reservations, and manage those reservations, catering to the needs of both vehicle owners and renters in a vehicle rental platform.


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
| `get("/authenticate")`| Authenticate a user by JWT  | [3]          | STAFF   |

#### User Endpoints (UserRoutes.kt)

| Endpoint              | Description                 | Endpoint No. | Access  |
|-----------------------|-----------------------------|--------------|---------|
| `get("/users")`       | Retrieve all users          | [3]          | STAFF   |
| `get("/{id}")`        | Retrieve a specific user     | [4]          | STAFF   |
| `put("/{id}")`        | Edit a specific user         | [5]          | STAFF   |
| `delete("/{id}")`     | Delete a specific user       | [6]          | STAFF   |
| `get("/me")`          | View own data                | [7]          | CLIENT  |
| `put("/me")`          | Modify own data              | [8]          | CLIENT  |
| `delete("/me")`       | Delete own account           | [9]          | CLIENT  |

#### Vehicle Endpoints (VehicleRoutes.kt)

| Endpoint                     | Description                 | Endpoint No. | Access  |
|------------------------------|-----------------------------|--------------|---------|
| `post("/createVehicle")`     | Register a vehicle          | [10]         | CLIENT  |
| `get("/{id}")`               | Retrieve a specific vehicle | [11]         | CLIENT  |
| `get("/all")`                | Retrieve all vehicles       | [12]         | STAFF   |
| `get("/allAvailable")`       | Retrieve available vehicles | [13]         | CLIENT  |
| `get("/user")`               | Retrieve vehicles of a user | [14]         | CLIENT  |
| `put("/{id}")`               | Edit a specific vehicle     | [15]         | CLIENT  |
| `put("/setAvailability/{id}/{availability}")` | Edit availability of a specific vehicle | [16] | CLIENT |
| `delete("/{id}")`            | Delete a specific vehicle   | [17]         | CLIENT  |

#### Rental Endpoints (RentalRoutes.kt)

| Endpoint                             | Description                 | Endpoint No. | Access  |
|--------------------------------------|-----------------------------|--------------|---------|
| `post("/createRental/{vehicleId}")` | Create a rental for one vehicle | [18]     | CLIENT  |
| `get("/rentals")`                   | Retrieve own rentals        | [19]         | CLIENT  |
| `get("/{id}")`                      | Retrieve a specific rental   | [20]         | CLIENT  |
| `get("/rentedVehicle/{id})`         | Retrieve rentals of one vehicle | [21]     | CLIENT  |
| `get("/ownedVehicle/{id})`          | Retrieve specific rentals    | [22]         | CLIENT  |
| `get("/allRentals")`                | Retrieve all rentals         | [23]         | STAFF   |
| `get("/{id}/{status}")`             | Modify status of one rental  | [24]         | CLIENT  |
| `put("/{id}")`                      | Edit a specific rental       | [25]         | STAFF   |
| `delete("/{id}")`                   | Delete a specific rental     | [26]         | CLIENT  |
| `delete("/staff/{id}")`             | Delete a specific rental     | [27]         | STAFF   |



## Contributing

### How to Contribute

We welcome contributions from the community to help improve our project. You can contribute in various ways:

- **Bug Reports**: If you come across any issues or bugs, please report them on our [GitHub repository](https://github.com/Digital-Architects-Avans/rmc-api). Be sure to provide detailed information about the problem, including steps to reproduce it.

- **Feature Requests**: If you have ideas for new features or enhancements, we'd love to hear them. Open an issue on GitHub with a clear description of the feature you're suggesting.

- **Code Contributions**: If you're interested in contributing code, feel free to fork our repository and submit pull requests. We encourage you to follow our coding style and guidelines.

Additionally, you can reach out to our team by opening a discussion on our GitHub repository for any questions or collaboration opportunities.

We appreciate your support and look forward to working together to make our project even better.

## License

This project is released under the [Apache License 2.0](LICENSE).

### Terms of Use and Distribution

By using this project, you agree to be bound by the terms and conditions of the Apache License 2.0. You may use, modify, distribute, and contribute to this project, subject to the terms specified in the license. Please review the [LICENSE](LICENSE) file for the full text of the Apache License 2.0 and understand its implications. If you have any questions or concerns, please contact the project owner for clarification.

Your use and contributions to this project are greatly appreciated, and we encourage collaboration in accordance with the terms of the Apache License 2.0.

