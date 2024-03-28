Parking System API Readme

Introduction

The Parking System API is designed to streamline the parking process for various entities such as malls, stadium, and airports. 
With intuitive endpoints and clear response structures, developers can easily integrate the API into their applications to facilitate parking management and enhance user experience.

Parking

Method- POST  

URL- /parking  

Request-  

{ 

  "parkingEntityName" : "mall",  

  "parkingLotName" : "mallA",

  "vehicleType" : "motorcycle"  

}  

If available, 

Response-  

{ 

  "ticketId" : "1001",  

  "parkingSpotId" : "101",  

  "entryTime" : "2024-03-14T12:28:06.005",  

  "message" : "null" 

}  

If not available, 

Response-  

{

  “ticketId” : “null”,  

  “parkingSpotId” : “null“,  

  “entryTime” : “null“,    

  “message” : “Parking spot full”  

}  

Unparking 

Method- PUT  

URL- /unparking 

Request- 

{ 

  “ticketId" : “1001” 

}

If valid, 

Response-  

{ 

  “ticketId” : “1001”,  

  “entryTime” : “2024-03-14T12:28:06.005“,  

  “exitTime” : “2024-03-14T03:30:09.005“,  

  “amount” : “120.00“, 

  “message” : “vehicle unparked”

} 

If invalid,

Response-

{

  “ticketId” : “null”,  

  “entryTime” : “null“,  

  “exitTime” : “null“,

  “message” : “ticketID not found”

}

Usage

To park a vehicle, send a POST request to the /parking endpoint with the required parameters. If a parking spot is available, the API will return a parking ticket containing essential details. 
To unpark a vehicle, send a PUT request to the /unparking endpoint with the ticket ID. Upon successful unparking, the API will provide details including the exit time and amount to be paid.

Notes

- Ensure the request payloads are formatted correctly according to the provided examples.
- Handle the responses appropriately based on the conditions outlined in the readme.
- For any issues or inquiries, please contact the system administrator.

This readme provides essential information for integrating with the Parking System API.
