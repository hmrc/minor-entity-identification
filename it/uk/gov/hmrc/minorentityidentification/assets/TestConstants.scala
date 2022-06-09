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

package uk.gov.hmrc.minorentityidentification.assets

import play.api.libs.json.{JsObject, Json}

import uk.gov.hmrc.minorentityidentification.connectors.RegisterWithMultipleIdentifiersHttpParser._

import java.util.UUID

object TestConstants {

  lazy val testJourneyId: String = UUID.randomUUID().toString
  lazy val testInternalId: String = UUID.randomUUID().toString
  val testSafeId: String = UUID.randomUUID().toString
  val testSautr: String = "1234567890"
  val testCtutr: String = "1234229999"
  val testRegime: String = "VATC"
  val testPostcode: String = "NE98 1ZZ"

  val testCompanyDetailsJson: JsObject = Json.obj("companyName" -> "ACME", "companyAddress" -> "Address 1", "companyPostCode" -> "NE98 1ZZ")

  val testRegistrationTrustJsonBody: JsObject = Json.obj("sautr" -> testSautr, "regime" -> testRegime)
  val testRegistrationUAJsonBody: JsObject = Json.obj("ctutr" -> testCtutr, "regime" -> testRegime)

  val registrationInternalServerErrorAsString: String =
    s"""
       |{
       |  "code" : "SERVER_ERROR",
       |  "reason" : "DES is currently experiencing problems that require live service intervention"
       | }
       |""".stripMargin

  val registrationInternalServerErrorAsJson: JsObject = Json.parse(registrationInternalServerErrorAsString).as[JsObject]

  val internalServerErrorFailure: Failure = Failure("SERVER_ERROR", "DES is currently experiencing problems that require live service intervention")

  val registrationMultipleFailureResultAsString: String =
    s"""
       |{
       |  "failures" : [
       |    {
       |      "code" : "INVALID_REGIME",
       |      "reason" : "Request has not passed validation. Invalid Regime."
       |    },
       |    {
       |      "code" : "INVALID_PAYLOAD",
       |      "reason" : "Request has not passed validation. Invalid Payload."
       |    }
       |  ]
       |}""".stripMargin

  val registrationMultipleFailureResultAsJson: JsObject = Json.parse(registrationMultipleFailureResultAsString).as[JsObject]

  val invalidRegimeFailure: Failure = Failure("INVALID_REGIME", "Request has not passed validation. Invalid Regime.")
  val invalidPayloadFailure: Failure = Failure("INVALID_PAYLOAD", "Request has not passed validation. Invalid Payload.")

  val invalidSuccessResponseAsString: String =
    s"""
       |{
       |  "identification": {}
       |}""".stripMargin

  val invalidSuccessResponseAsJson: JsObject = Json.parse(invalidSuccessResponseAsString).as[JsObject]

  val invalidSingleErrorAsJson: JsObject = Json.parse("{}").as[JsObject]

  val invalidMultipleErrorsAsString: String =
    s"""
       |{
       |  "failures": [{}]
       |}
       |""".stripMargin

  val invalidMultipleErrorsAsJson: JsObject = Json.parse(invalidMultipleErrorsAsString).as[JsObject]

  val registrationControllerSingleFailureResultAsString: String =
    s"""
       |{
       |  "registration" : {
       |    "registrationStatus" : "REGISTRATION_FAILED",
       |    "failures" : [
       |      {
       |        "code" : "SERVER_ERROR",
       |        "reason" : "DES is currently experiencing problems that require live service intervention"
       |      }
       |    ]
       |  }
       |}""".stripMargin

  val registrationControllerSingleFailureResultAsJson: JsObject = Json.parse(registrationControllerSingleFailureResultAsString).as[JsObject]

  val registrationControllerMultipleFailureResultAsString: String =
    s"""
       |{
       |  "registration" : {
       |    "registrationStatus" : "REGISTRATION_FAILED",
       |    "failures" : [
       |      {
       |        "code" :  "INVALID_REGIME",
       |        "reason" : "Request has not passed validation. Invalid Regime."
       |      },
       |      {
       |        "code" :  "INVALID_PAYLOAD",
       |        "reason" : "Request has not passed validation. Invalid Payload."
       |      }
       |    ]
       |  }
       |}""".stripMargin

  val registrationControllerMultipleFailureResultAsJson: JsObject = Json.parse(registrationControllerMultipleFailureResultAsString).as[JsObject]
}
