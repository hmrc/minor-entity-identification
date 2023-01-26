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

package uk.gov.hmrc.minorentityidentification.services

import org.mockito.scalatest.{IdiomaticMockito, ResetMocksAfterEachTest}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.minorentityidentification.connectors.GetCtReferenceConnector
import uk.gov.hmrc.minorentityidentification.models.{DetailsMatched, DetailsMismatched, DetailsNotFound}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ValidateCtDetailsServiceSpec extends AnyWordSpec with Matchers with IdiomaticMockito with ResetMocksAfterEachTest {
  val mockGetCtReferenceConnector: GetCtReferenceConnector = mock[GetCtReferenceConnector]

  object TestValidateCtutrService extends ValidateCtDetailsService(mockGetCtReferenceConnector)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val testCtutr: String = "1234229999"
  val testPostcode: String = "NE98 1ZZ"

  "validateDetails" should {
    "return DetailsMatched" when {
      "the supplied postcode matches the retrieved postcode" in {
        mockGetCtReferenceConnector.getCtReference(testCtutr) returns Future.successful(Some(testPostcode))

        await(TestValidateCtutrService.validateDetails(testCtutr, testPostcode)) mustBe DetailsMatched
      }
      "the postcodes is lower case but matches" in {
        mockGetCtReferenceConnector.getCtReference(testCtutr) returns Future.successful(Some(testPostcode))

        await(TestValidateCtutrService.validateDetails(testCtutr, "ne981zz")) mustBe DetailsMatched
      }
    }
    "return DetailsMismatched" when {
      "the supplied postcode does not match the retrieved postcode" in {
        val mismatchedPostcode = "AA1 1AA"
        mockGetCtReferenceConnector.getCtReference(testCtutr) returns Future.successful(Some(testPostcode))

        await(TestValidateCtutrService.validateDetails(testCtutr, mismatchedPostcode)) mustBe DetailsMismatched
      }
    }
    "return DetailsNotFound" when {
      "when the call returns 404" in {
        mockGetCtReferenceConnector.getCtReference(testCtutr) returns Future.successful(None)

        await(TestValidateCtutrService.validateDetails(testCtutr, testPostcode)) mustBe DetailsNotFound
      }
    }
  }
}
