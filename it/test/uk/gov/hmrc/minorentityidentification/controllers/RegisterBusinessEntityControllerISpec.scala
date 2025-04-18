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

import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.test.HttpClientV2Support
import uk.gov.hmrc.minorentityidentification.assets.TestConstants._
import uk.gov.hmrc.minorentityidentification.stubs.{AuthStub, RegisterWithMultipleIdentifiersStub}
import uk.gov.hmrc.minorentityidentification.utils.ComponentSpecHelper

import javax.inject.Singleton

@Singleton
class RegisterBusinessEntityControllerISpec extends ComponentSpecHelper with AuthStub with HttpClientV2Support with RegisterWithMultipleIdentifiersStub {

  "POST /register-trust" should {
    "return OK with status Registered and the SafeId" when {
      "the Registration was a success with a SAUTR" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterTrustSuccess(testSautr, testRegime)(OK, testSafeId)

        val resultJson = Json.obj(
          "registration" -> Json.obj(
            "registrationStatus" -> "REGISTERED",
            "registeredBusinessPartnerId" -> testSafeId))

        val result = post("/register-trust")(testRegistrationTrustJsonBody)
        result.status mustBe OK
        result.json mustBe resultJson
      }
    }
    "return an error response" when {
      "the Registration was not successful owing to a single error" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterTrustFailure(testSautr, testRegime)(INTERNAL_SERVER_ERROR, registrationInternalServerErrorAsJson)

        val result = post("/register-trust")(testRegistrationTrustJsonBody)
        result.status mustBe OK
        result.json mustBe registrationControllerSingleFailureResultAsJson
      }
      "the Registration was not successful owing to multiple errors" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterTrustFailure(testSautr, testRegime)(BAD_REQUEST, registrationMultipleFailureResultAsJson)

        val result = post("/register-trust")(testRegistrationTrustJsonBody)
        result.status mustBe OK
        result.json mustBe registrationControllerMultipleFailureResultAsJson
      }
    }
    "return an internal server error " when {
      "the Registration was not successful owing to invalid Json in the response" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterTrustFailure(testSautr, testRegime)(BAD_REQUEST, invalidMultipleErrorsAsJson)

        val result = post("/register-trust")(testRegistrationTrustJsonBody)
        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "POST /register-ua" should {
    "return OK with status Registered and the SafeId" when {
      "the Registration was a success with a CTUTR" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterUASuccess(testCtutr, testRegime)(OK, testSafeId)

        val resultJson = Json.obj(
          "registration" -> Json.obj(
            "registrationStatus" -> "REGISTERED",
            "registeredBusinessPartnerId" -> testSafeId))

        val result = post("/register-ua")(testRegistrationUAJsonBody)
        result.status mustBe OK
        result.json mustBe resultJson
      }
    }
    "return an error response" when {
      "the Registration is not successful owing to a single error" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterUAFailure(testCtutr, testRegime)(INTERNAL_SERVER_ERROR, registrationInternalServerErrorAsJson)

        val result = post("/register-ua")(testRegistrationUAJsonBody)
        result.status mustBe OK
        result.json mustBe registrationControllerSingleFailureResultAsJson
      }
      "the Registration was not successful owing to multiple errors" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterUAFailure(testCtutr, testRegime)(BAD_REQUEST, registrationMultipleFailureResultAsJson)

        val result = post("/register-ua")(testRegistrationUAJsonBody)
        result.status mustBe OK
        result.json mustBe registrationControllerMultipleFailureResultAsJson
      }
    }
    "return an internal server error " when {
      "the Registration was not successful owing to invalid Json in the response" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterUAFailure(testCtutr, testRegime)(BAD_REQUEST, invalidMultipleErrorsAsJson)

        val result = post("/register-ua")(testRegistrationUAJsonBody)
        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
