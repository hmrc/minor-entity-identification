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

package uk.gov.hmrc.minorentityidentification.connectors

import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.minorentityidentification.assets.TestConstants.{testRegime, testSafeId, testSautr}
import uk.gov.hmrc.minorentityidentification.connectors.RegisterWithMultipleIdentifiersHttpParser.RegisterWithMultipleIdentifiersSuccess
import uk.gov.hmrc.minorentityidentification.featureswitch.core.config.{DesStub, FeatureSwitching}
import uk.gov.hmrc.minorentityidentification.stubs.RegisterWithMultipleIdentifiersStub
import uk.gov.hmrc.minorentityidentification.utils.ComponentSpecHelper

class RegisterWithMultipleIdentifiersConnectorISpec extends ComponentSpecHelper with RegisterWithMultipleIdentifiersStub with FeatureSwitching {

  lazy val connector: RegisterWithMultipleIdentifiersConnector = app.injector.instanceOf[RegisterWithMultipleIdentifiersConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "registerWithMultipleIdentifiers" when {
    s"the $DesStub feature switch is disabled" should {
      "return OK with status Registered and the SafeId" when {
        "the Registration was a success on the Register API stub with a SAUTR" in {
          disable(DesStub)

          stubRegisterWithSautrSuccess(testSautr, testRegime)(OK, testSafeId)
          val result = connector.registerWithSautr(testSautr, testRegime)
          await(result) mustBe RegisterWithMultipleIdentifiersSuccess(testSafeId)
        }
      }

      "throw an Internal Server Exception" when {
        "the call returns 500" in {
          disable(DesStub)

          stubRegisterWithSautrSuccess(testSautr, testRegime)(INTERNAL_SERVER_ERROR, testSafeId)
          intercept[InternalServerException](await(connector.registerWithSautr(testSautr, testRegime)))
        }
      }
    }
    s"the $DesStub feature switch is enabled" when {
      "return OK with status Registered and the SafeId" when {
        "the Registration was a success on the Register API stub with a SAUTR" in {
          enable(DesStub)

          stubRegisterWithSautrSuccess(testSautr, testRegime)(OK, testSafeId)
          val result = connector.registerWithSautr(testSautr, testRegime)
          await(result) mustBe RegisterWithMultipleIdentifiersSuccess(testSafeId)
        }
      }

      "throw an Internal Server Exception" when {
        "the call returns 500" in {
          enable(DesStub)

          stubRegisterWithSautrSuccess(testSautr, testRegime)(INTERNAL_SERVER_ERROR, testSafeId)
          intercept[InternalServerException](await(connector.registerWithSautr(testSautr, testRegime)))
        }
      }
    }
  }

}
