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

package uk.gov.hmrc.minorentityidentification.connectors

import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.minorentityidentification.assets.TestConstants._
import uk.gov.hmrc.minorentityidentification.connectors.RegisterWithMultipleIdentifiersHttpParser._
import uk.gov.hmrc.minorentityidentification.featureswitch.core.config.{DesStub, FeatureSwitching}
import uk.gov.hmrc.minorentityidentification.stubs.RegisterWithMultipleIdentifiersStub
import uk.gov.hmrc.minorentityidentification.utils.ComponentSpecHelper

class RegisterWithMultipleIdentifiersConnectorISpec extends ComponentSpecHelper with RegisterWithMultipleIdentifiersStub with FeatureSwitching {

  lazy val connector: RegisterWithMultipleIdentifiersConnector = app.injector.instanceOf[RegisterWithMultipleIdentifiersConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  val jsonBodyTrust: JsObject = Json.obj(
    "trust" -> Json.obj(
      "sautr" -> testSautr
    )
  )

  val jsonBodyUA: JsObject = Json.obj(
    "unincorporatedAssociation" -> Json.obj(
      "ctutr" -> testCtutr
    )
  )

  "register" when {
    s"the $DesStub feature switch is disabled" should {
      "return OK with status Registered and the SafeId" when {
        "the Registration was a success with a SAUTR" in {
          disable(DesStub)

          stubRegisterTrustSuccess(testSautr, testRegime)(OK, testSafeId)
          val result = connector.register(jsonBodyTrust, testRegime)
          await(result) mustBe RegisterWithMultipleIdentifiersSuccess(testSafeId)
        }
        "the Registration was a success with a CTUTR" in {
          disable(DesStub)

          stubRegisterUASuccess(testCtutr, testRegime)(OK, testSafeId)
          val result = connector.register(jsonBodyUA, testRegime)
          await(result) mustBe RegisterWithMultipleIdentifiersSuccess(testSafeId)
        }
      }

      "return a registration failed result with a single failure" when {
        "the call returns 500" in {
          disable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(INTERNAL_SERVER_ERROR, registrationInternalServerErrorAsJson)

          await(connector.register(jsonBodyTrust, testRegime)) match {
            case RegisterWithMultipleIdentifiersFailure(status, body) =>
              status mustBe INTERNAL_SERVER_ERROR
              body.length mustBe 1
              body.head mustBe internalServerErrorFailure
            case _ => fail("Registration failure result expected")
          }

        }
      }
      "return a registration failed result with multiple failures" when {
        "the call returns 400 with multiple errors" in {
          disable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(BAD_REQUEST, registrationMultipleFailureResultAsJson)

          await(connector.register(jsonBodyTrust, testRegime)) match {
            case RegisterWithMultipleIdentifiersFailure(status, body) =>
              status mustBe BAD_REQUEST
              body.length mustBe 2
              body.head mustBe invalidRegimeFailure
              body.last mustBe invalidPayloadFailure
            case _ => fail("Registration failure result with multiple failures expected")
          }
        }
      }
      "raise an internal server exception" when {
        "the call returns status Ok, but the Json payload is invalid" in {
          disable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(OK, invalidSuccessResponseAsJson)

          intercept[InternalServerException](await(connector.register(jsonBodyTrust, testRegime)))
        }
        "the call returns invalid Json for a single failure" in {
          disable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(INTERNAL_SERVER_ERROR, invalidSingleErrorAsJson)

          intercept[InternalServerException](await(connector.register(jsonBodyTrust, testRegime)))
        }
        "the call returns invalid json for multiple failures" in {
          disable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(INTERNAL_SERVER_ERROR, invalidMultipleErrorsAsJson)

          intercept[InternalServerException](await(connector.register(jsonBodyTrust, testRegime)))
        }
      }
    }
    s"the $DesStub feature switch is enabled" when {
      "return OK with status Registered and the SafeId" when {
        "the Registration was a success with a SAUTR" in {
          enable(DesStub)

          stubRegisterTrustSuccess(testSautr, testRegime)(OK, testSafeId)
          val result = connector.register(jsonBodyTrust, testRegime)
          await(result) mustBe RegisterWithMultipleIdentifiersSuccess(testSafeId)
        }
        "the Registration was a success with a CTUTR" in {
          enable(DesStub)

          stubRegisterUASuccess(testCtutr, testRegime)(OK, testSafeId)
          val result = connector.register(jsonBodyUA, testRegime)
          await(result) mustBe RegisterWithMultipleIdentifiersSuccess(testSafeId)
        }
      }

      "return a registration failed result with a single failure" when {
        "the call returns 500" in {
          enable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(INTERNAL_SERVER_ERROR, registrationInternalServerErrorAsJson)

          await(connector.register(jsonBodyTrust, testRegime)) match {
            case RegisterWithMultipleIdentifiersFailure(status, body) =>
              status mustBe INTERNAL_SERVER_ERROR
              body.length mustBe 1
              body.head mustBe internalServerErrorFailure
            case _ => fail("Registration failure result expected")
          }
        }
      }
      "return a registration failed result with multiple failures" when {
        "the call returns 400 with multiple errors" in {
          enable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(BAD_REQUEST, registrationMultipleFailureResultAsJson)

          await(connector.register(jsonBodyTrust, testRegime)) match {
            case RegisterWithMultipleIdentifiersFailure(status, body) =>
              status mustBe BAD_REQUEST
              body.length mustBe 2
              body.head mustBe invalidRegimeFailure
              body.last mustBe invalidPayloadFailure
            case _ => fail("Registration failure result with multiple failures expected")
          }
        }
      }
      "raise an internal server exception" when {
        "the call returns status Ok, but the Json payload is invalid" in {
          enable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(OK, invalidSuccessResponseAsJson)

          intercept[InternalServerException](await(connector.register(jsonBodyTrust, testRegime)))
        }
        "the call returns invalid Json for a single failure" in {
          enable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(INTERNAL_SERVER_ERROR, invalidSingleErrorAsJson)

          intercept[InternalServerException](await(connector.register(jsonBodyTrust, testRegime)))
        }
        "the call returns invalid json for multiple failures" in {
          enable(DesStub)

          stubRegisterTrustFailure(testSautr, testRegime)(INTERNAL_SERVER_ERROR, invalidMultipleErrorsAsJson)

          intercept[InternalServerException](await(connector.register(jsonBodyTrust, testRegime)))
        }
      }
    }
  }
}
