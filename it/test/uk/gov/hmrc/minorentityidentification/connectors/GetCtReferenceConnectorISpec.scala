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

import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.test.HttpClientV2Support
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.minorentityidentification.assets.TestConstants.{testCompanyDetailsJson, testCtutr, testPostcode}
import uk.gov.hmrc.minorentityidentification.featureswitch.core.config.{DesStub, FeatureSwitching}
import uk.gov.hmrc.minorentityidentification.stubs.GetCtReferenceStub
import uk.gov.hmrc.minorentityidentification.utils.ComponentSpecHelper

import scala.concurrent.ExecutionContext

class GetCtReferenceConnectorISpec extends ComponentSpecHelper with FeatureSwitching with HttpClientV2Support with GetCtReferenceStub {
  private implicit val ec: ExecutionContext = ExecutionContext.global
  lazy val connector: GetCtReferenceConnector = new GetCtReferenceConnector(httpClientV2, appConfig) {

  }

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "getCtReference()" when {
    "the DES Feature switch is disabled" should {
      "return Some(1234229999)" in {
        disable(DesStub)
        stubGetCtReference(testCtutr)(OK, body = Json.obj("companyName" -> "ACME", "companyAddress" -> "Address 1", "companyPostCode" -> "NE98 1ZZ"))

        val result = await(connector.getCtReference(testCtutr))

        result mustBe Some(testPostcode)
      }
      "return None" when {
        "the call returns 404" in {
          disable(DesStub)
          stubGetCtReference(testCtutr)(NOT_FOUND)

          val result = await(connector.getCtReference(testCtutr))

          result mustBe None
        }
      }
      "throw and internal server exception" when {
        "a different status is returned" in {
          disable(DesStub)
          stubGetCtReference(testCtutr)(BAD_REQUEST)

          intercept[InternalServerException](await(connector.getCtReference(testCtutr)))
        }
      }
    }
    "the DES Feature switch is enabled" should {
      "return Some(1234229999)" in {
        enable(DesStub)
        stubGetCtReference(testCtutr)(OK, body = testCompanyDetailsJson)

        val result = await(connector.getCtReference(testCtutr))

        result mustBe Some(testPostcode)
      }
      "return None" when {
        "the call returns 404" in {
          enable(DesStub)
          stubGetCtReference(testCtutr)(NOT_FOUND)

          val result = await(connector.getCtReference(testCtutr))

          result mustBe None
        }
      }
      "throw and internal server exception" when {
        "a different status is returned" in {
          enable(DesStub)
          stubGetCtReference(testCtutr)(BAD_REQUEST)

          intercept[InternalServerException](await(connector.getCtReference(testCtutr)))
        }
      }
    }
  }
}
