# Minor Entity Identification Test End-Points

## Testing

---

1. [Setting up Feature Switches](TestREADME.md#feature-switches)

### Feature Switches

---

- Use stub for submissions to Registration (see [here](TestREADME.md#post-test-onlycross-regimeregistergrs))
- Use stub for Get CT Reference (see [here](TestREADME.md#get-corporation-taxidentifiersutrctutr))

### POST /test-only/cross-regime/register/GRS

---
Stub for downstream Register API

##### Request:

Body:

Trust:

```
{
  "trust": { "sautr":"1234567890"}
}
```

Unincorporated Association:

```
{
  "unincorporatedAssociation": { "ctutr":"1234567890"}
}
```


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

or if the identifier is "0000000001" a single
failure response is returned

```
{
  "code"   : "INVALID_PAYLOAD",
  "reason" : "Request has not passed validation. Invalid Payload."
}
```

or if the identifier is "0000000002" a multiple
failure response is returned

```
{
    "failures" : [
      {
        "code" : "INVALID_PAYLOAD",
        "reason" : "Request has not passed validation. Invalid Payload."
      },
      {
        "code" : "INVALID_REGIME",
        "reason" : "Request has not passed validation. Invalid Regime."
      }
    ]
}
```

### GET /corporation-tax/identifiers/utr/:ctUtr

---

Returns company post code based on ctutr entered.

#### Request:
URI must contain a ctutr.

Ctutrs are mapped to specific postcodes

| CTUTR                      | Postcode         |
|----------------------------|------------------|
| ```0000000000```           | ```NOT FOUND```  |
| ```Anything valid CTUTR``` | ```NE98 1ZZ```   |


#### Response:

| Expected Status                         | Reason                                 |
|-----------------------------------------|----------------------------------------|
| ```OK(200)```                           | ```Postcode found```                   |
| ```NOT_FOUND(404)```                    | ```postcode not found in database```   |

Example Response body:

```
"{companyPostCode":"NE98 1ZZ}"
```
