/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.minorentityidentification.controllers

import play.api.http.Status.{BAD_REQUEST, UNAUTHORIZED}
import play.api.libs.json.Json
import play.api.test.Helpers.{NOT_FOUND, OK}
import uk.gov.hmrc.minorentityidentification.assets.TestConstants.{testCompanyDetailsJson, testCtutr, testInternalId, testPostcode}
import uk.gov.hmrc.minorentityidentification.stubs.{AuthStub, GetCtReferenceStub}
import uk.gov.hmrc.minorentityidentification.utils.ComponentSpecHelper
import uk.gov.hmrc.http.test.HttpClientV2Support

class ValidateCtDetailsControllerISpec extends ComponentSpecHelper with AuthStub with HttpClientV2Support with GetCtReferenceStub {

  "validateDetails" should {
    "return details match" when {
      "supplied details match those in database" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubGetCtReference(testCtutr)(status = OK, body = testCompanyDetailsJson)

        val suppliedJson = Json.obj(
          "ctutr" -> testCtutr,
          "postcode" -> testPostcode
        )

        val result = post("/validate-details")(suppliedJson)

        result.status mustBe OK
        result.json mustBe Json.obj("matched" -> true)
      }
    }

    "return details do not match" when {
      "supplied details do not match those in database" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubGetCtReference(testCtutr)(status = OK, body = testCompanyDetailsJson)

        val suppliedJson = Json.obj(
          "ctutr" -> testCtutr,
          "postcode" -> "mistmatch"
        )

        val result = post("/validate-details")(suppliedJson)

        result.status mustBe OK
        result.json mustBe Json.obj("matched" -> false)
      }
    }

    "return details not found" when {
      "supplied details are not found in database" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubGetCtReference(testCtutr)(status = NOT_FOUND)

        val testJson = Json.obj(
          "code" -> "NOT_FOUND",
          "reason" -> "The back end has indicated that CT UTR cannot be returned"
        )

        val suppliedJson = Json.obj(
          "ctutr" -> testCtutr,
          "postcode" -> "mistmatch"
        )

        val result = post("/validate-details")(suppliedJson)

        result.status mustBe BAD_REQUEST
        result.json mustBe testJson
      }
    }
    "return Unauthorised" when {
      "there is an auth failure" in {
        stubAuthFailure()
        stubGetCtReference(testCtutr)(status = OK, body = testCompanyDetailsJson)

        val suppliedJson = Json.obj(
          "ctutr" -> testCtutr,
          "postcode" -> "mistmatch"
        )

        val result = post("/validate-details")(suppliedJson)

        result.status mustBe UNAUTHORIZED
      }
    }
  }

}

