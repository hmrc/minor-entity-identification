/*
 * Copyright 2025 HM Revenue & Customs
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

import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.ArgumentMatchers.{eq => eqTo}
import org.mockito.Mockito._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.minorentityidentification.repositories.JourneyDataRepository

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class JourneyDataServiceSpec extends AnyWordSpec with Matchers {
  val mockJourneyDataRepository: JourneyDataRepository = mock[JourneyDataRepository]
  val mockJourneyIdGenerationService: JourneyIdGenerationService = mock[JourneyIdGenerationService]

  object TestJourneyDataService extends JourneyDataService(mockJourneyDataRepository, mockJourneyIdGenerationService)

  val testJourneyId: String = UUID.randomUUID().toString
  val testInternalId: String = "testInternalId"

  "createJourney" should {
    "call to store a new journey with the generated journey ID" in {
      when(mockJourneyIdGenerationService.generateJourneyId()).thenReturn(testJourneyId)
      when(mockJourneyDataRepository.createJourney(eqTo(testJourneyId), eqTo(testInternalId))).thenReturn(Future.successful(testJourneyId))

      await(TestJourneyDataService.createJourney(testInternalId)) mustBe testJourneyId
    }
  }

  "getJourneyData" should {
    "return the stored journey data" when {
      "the data exists in the database" in {
        val testJourneyData = Json.obj("testKey" -> "testValue")

        when(mockJourneyDataRepository.getJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(Some(testJourneyData)))

        await(TestJourneyDataService.getJourneyData(testJourneyId, testInternalId)) mustBe Some(testJourneyData)
      }
    }

    "return None" when {
      "the data does not exist in the database" in {
        when(mockJourneyDataRepository.getJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(None))

        await(TestJourneyDataService.getJourneyData(testJourneyId, testInternalId)) mustBe None
      }
    }
  }

  "getJourneyDataByKey" should {
    "return the stored journey data for the key provided" when {
      "the data exists in the database" in {
        val testKey = "testKey"
        val testValue = "testValue"

        val testJourneyData = Json.obj(testKey -> testValue)

        when(mockJourneyDataRepository.getJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(Some(testJourneyData)))

        await(TestJourneyDataService.getJourneyDataByKey(testJourneyId, testKey, testInternalId)) mustBe Some(JsString(testValue))

      }
    }

    "return None" when {
      "the data does not exist in the database" in {
        val testKey = "testKey"

        when(mockJourneyDataRepository.getJourneyData(testJourneyId, testInternalId)).thenReturn(Future.successful(None))

        await(TestJourneyDataService.getJourneyDataByKey(testJourneyId, testKey, testInternalId)) mustBe None
      }
    }
  }

  "updateJourneyData" should {
    "return true" when {
      "the data field exists and has been updated" in {
        val testKey = "testKey"
        val testValue = JsString("testValue")

        when(mockJourneyDataRepository.updateJourneyData(testJourneyId, testInternalId, testKey, testValue)).thenReturn(Future.successful(true))

        await(TestJourneyDataService.updateJourneyData(testJourneyId, testInternalId, testKey, testValue)) mustBe true
      }
    }

    "return false" when {
      "the field does not exist" in {
        val testKey = "testKey"
        val testValue = JsString("testValue")

        when(mockJourneyDataRepository.updateJourneyData(testJourneyId, testInternalId, testKey, testValue)).thenReturn(Future.successful(false))

        await(TestJourneyDataService.updateJourneyData(testJourneyId, testInternalId, testKey, testValue)) mustBe false
      }
    }
  }

  "removeJourneyData" should {
    "return true" when {
      "the data field exist and has been removed" in {
        val testKey = "testKey"

        when(mockJourneyDataRepository.removeJourneyDataField(testJourneyId, testInternalId, testKey)).thenReturn(Future.successful(true))

        await(TestJourneyDataService.removeJourneyDataField(testJourneyId, testInternalId, testKey)) mustBe true
      }
    }

    "return false" when {
      "the data field does not exist" in {
        val testKey = "testKey"

        when(mockJourneyDataRepository.removeJourneyDataField(testJourneyId, testInternalId, testKey)).thenReturn(Future.successful(false))

        await(TestJourneyDataService.removeJourneyDataField(testJourneyId, testInternalId, testKey)) mustBe false
      }
    }
  }
}

