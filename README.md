# Minor Entity Identification

This is a Scala/Play backend service supporting minor-entity-identification-frontend to allow Minor Entities to provide their information to HMRC.

### How to run the service
1. Make sure any dependent services are running using the following service-manager command `sm --start MINOR_ENTITY_IDENTIFICATION_ALL -r`
2. Stop the backend in service manager using `sm --stop MINOR_ENTITY_IDENTIFICATION`
3. Run the back locally using
   `sbt 'run 9726 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes'`

### End-Points

#### POST /journey

---
Creates a new journeyId and stores it in the database

##### Request:
No body is required for this request

##### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyId”: "<random UUID>"}
```

#### GET /journey/:journeyId

---
Retrieves all the journey data that is stored against a specific journeyID.

##### Request:
A valid journeyId must be sent in the URI

##### Response

| Expected Status                         | Reason                           |
|-----------------------------------------|----------------------------------|
| ```OK(200)```                           | ```JourneyId exists```           |
| ```NOT_FOUND(404)```                    | ```JourneyId does not exist```   |

Example response body:
```
{
"tbc":"test"
}
```

#### GET /journey/:journeyId/:dataKey

---
Retrieves all the journey data that matches the dataKey for a specific journeyID.

##### Request:
Example Request URI

`testJourneyId = <random UUID>`
```
/journey/testJourneyId/tbc
```

##### Response:

| Expected Status                         | Reason                                        |
|-----------------------------------------|-----------------------------------------------|
| ```OK(200)```                           | ```JourneyId exists```                        |
| ```NOT_FOUND(404)```                    | ```No data exists for JourneyId or dataKey``` |
| ```FORBIDDEN(403)```                    | ```Auth Internal IDs do not match```          |


Response body for example URI:
```
{"test"}
```

#### PUT /journey/:journeyId/:dataKey

---
Stores the json body against the data key and journey id provided in the uri

##### Request:
Requires a valid journeyId and user must be authorised to make changes to the data

Example request URI:
`testJourneyId = <random UUID>`
```
/journey/testJourneyId/tbc
```

Example request body:
```
{"test"}
```
##### Response:

| Expected Status                         | Reason                                 |
|-----------------------------------------|----------------------------------------|
| ```OK(200)```                           | ```OK```                               |
| ```FORBIDDEN(403)```                    | ```Auth Internal IDs do not match```   |


#### POST /register

---
Submits a registration request to the downstream Register API.
This API is feature switched behind the `Use stub for submissions to Registration` switch, so it can be stubbed using the Register test endpoint described below.

##### Request:
Body:

```
{
"minorEntity": {
            "sautr": 1234567890,
            "regime": "VATC"
           }
}
```

The property "regime" is used to define the associated GRS regime. Current valid values
are VATC and PPT.

##### Response:

Status: **OK(200)**
Attempted registration and returns result of call


Example response bodies:
```
{
"registration":{
                "registrationStatus":"REGISTERED",
                "registeredBusinessPartnerId":"<randomm UUID>"
               }
}
```
or
```
{
"registration":{
                "registrationStatus":"REGISTRATION_FAILED",
               }
}
```

#### DELETE /journey/:journeyId/:dataKey

---
Removes the data that is stored against the dataKey provided for the specific journeyId

##### Request:
Requires a valid journeyId and dataKey

Example request URI:
`testJourneyId = <random UUID>`
```
/journey/testJourneyId/utr
```

##### Response:

| Expected Status                         | Reason                                         |
|-----------------------------------------|------------------------------------------------|
| ```NO_CONTENT(204)```                   | ```Field successfully deleted from database``` |
| ```FORBIDDEN(403)```                    | ```Auth Internal IDs do not match```           |

#### DELETE /journey/:journeyId

---
Removes the data that is stored against the journeyId

##### Request:
Requires a valid journeyId and dataKey

Example request URI:
`testJourneyId = <random UUID>`
```
/journey/testJourneyId
```

##### Response:

| Expected Status                         | Reason                                        |
|-----------------------------------------|-----------------------------------------------|
| ```NO_CONTENT(204)```                   | ```Data successfully deleted from database``` |
| ```FORBIDDEN(403)```                    | ```Auth Internal IDs do not match```          |

#### POST /test-only/cross-regime/register/GRS

---
Stub for downstream Register API

##### Request:
No body is required for this request as this always returns a successful response regardless of the data sent.

##### Response:
Status: **OK(200)**

Example Response body:

```
{
"identification":{
                  "idType":"SAFEID",
                  "idValue":"X00000123456789"
                 }
}
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").