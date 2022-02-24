/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.minorentityidentification.assets.TestConstants.{testInternalId, testRegime, testSafeId, testSautr}
import uk.gov.hmrc.minorentityidentification.stubs.{AuthStub, RegisterWithMultipleIdentifiersStub}
import uk.gov.hmrc.minorentityidentification.utils.ComponentSpecHelper

import javax.inject.Singleton

@Singleton
class RegisterBusinessEntityControllerISpec extends ComponentSpecHelper with AuthStub with RegisterWithMultipleIdentifiersStub {

  "POST /register" should {
    "return OK with status Registered and the SafeId" when {
      "the Registration was a success with a SAUTR" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterWithSautrSuccess(testSautr, testRegime)(OK, testSafeId)

        val jsonBody = Json.obj(
          "trust" -> Json.obj(
            "sautr" -> testSautr,
            "regime" -> testRegime
          )
        )

        val resultJson = Json.obj(
          "registration" -> Json.obj(
            "registrationStatus" -> "REGISTERED",
            "registeredBusinessPartnerId" -> testSafeId))

        val result = post("/register")(jsonBody)
        result.status mustBe OK
        result.json mustBe resultJson
      }
    }
    "return INTERNAL_SERVER_ERROR" when {
      "the Registration was not successful" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRegisterWithSautrFailure(testSautr, testRegime)(BAD_REQUEST)

        val jsonBody = Json.obj(
          "trust" -> Json.obj(
            "sautr" -> testSautr,
            "regime" -> testRegime
          )
        )

        val result = post("/register")(jsonBody)
        result.status mustBe INTERNAL_SERVER_ERROR

      }
    }
  }
}
