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

package uk.gov.hmrc.minorentityidentification.repositories

import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.test.HttpClientV2Support
import uk.gov.hmrc.minorentityidentification.assets.TestConstants.{testInternalId, testJourneyId}
import uk.gov.hmrc.minorentityidentification.utils.{ComponentSpecHelper, JourneyDataMongoHelper}

import java.util.concurrent.TimeUnit

class JourneyDataRepositoryISpec extends ComponentSpecHelper with JourneyDataMongoHelper with HttpClientV2Support {

  override def beforeEach(): Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  val journeyIdKey: String = "_id"
  val authInternalIdKey: String = "authInternalId"
  val creationTimestampKey: String = "creationTimestamp"

  "createJourney" should {
    "successfully insert the journeyId" in {
      await(repo.createJourney(testJourneyId, testInternalId)) mustBe testJourneyId
    }
  }
  s"getJourneyData($testJourneyId)" should {
    "successfully return all data" in {
      insertById(testJourneyId, testInternalId, Json.obj(journeyIdKey -> testJourneyId, authInternalIdKey -> testInternalId))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.-(creationTimestampKey)) mustBe
        Some(Json.obj(journeyIdKey -> testJourneyId, authInternalIdKey -> testInternalId))
    }
  }
  "updateJourneyData" should {
    "successfully insert data" in {
      val testKey = "testKey"
      val testData = "test"
      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.updateJourneyData(testJourneyId, testInternalId, testKey, JsString(testData)))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(json => (json \ testKey).as[String]) mustBe Some(testData)
    }
    "successfully update data when data is already stored against a key" in {
      val testKey = "testKey"
      val testData = "test"
      val updatedData = "updated"
      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.updateJourneyData(testJourneyId, testInternalId, testKey, JsString(testData)))
      await(repo.updateJourneyData(testJourneyId, testInternalId, testKey, JsString(updatedData)))
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(json => (json \ testKey).as[String]) mustBe Some(updatedData)
    }

  }
  "removeJourneyDataField" should {
    "successfully remove a field" in {
      val testKey = "testKey"
      val testData = "test"

      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.updateJourneyData(testJourneyId, testInternalId, testKey, JsString(testData)))
      await(repo.removeJourneyDataField(testJourneyId, testInternalId, testKey)) mustBe true
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.-(creationTimestampKey)) mustBe
        Some(Json.obj(journeyIdKey -> testJourneyId, authInternalIdKey -> testInternalId))
    }
    "pass successfully when the field is not present" in {
      val testKey = "testKey"
      val testData = "test"
      val testSecondKey = "secondKey"

      await(repo.createJourney(testJourneyId, testInternalId))
      await(repo.updateJourneyData(testJourneyId, testInternalId, testKey, JsString(testData)))
      await(repo.removeJourneyDataField(testJourneyId, testInternalId, testSecondKey)) mustBe true
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.-(creationTimestampKey)) mustBe
        Some(Json.obj(journeyIdKey -> testJourneyId, authInternalIdKey -> testInternalId, testKey -> testData))
    }
  }
  "removeJourneyData" should {
    "successfully remove data associated with journeyId" in {
      val json = Json.obj(journeyIdKey -> testJourneyId,
        authInternalIdKey -> testInternalId,
        "testJson" -> Json.obj("testKey" -> "testValue", "secondTestKey" -> "secondTestValue"))

      insertById(testJourneyId, testInternalId, json)
      await(repo.removeJourneyData(testJourneyId, testInternalId)) mustBe true
      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.-(creationTimestampKey)) mustBe
        Some(Json.obj(journeyIdKey -> testJourneyId, authInternalIdKey -> testInternalId))

      await(repo.getJourneyData(testJourneyId, testInternalId)).map(_.keys.contains(creationTimestampKey)) mustBe Some(true)
    }
    "return false if data associated with journeyId cannot be found" in {
      val json = Json.obj(journeyIdKey -> testJourneyId,
        authInternalIdKey -> testInternalId,
        "testJson" -> Json.obj("testKey" -> "testValue", "secondTestKey" -> "secondTestValue"))

      val testWrongJourneyId = "11111111"

      insertById(testJourneyId, testInternalId, json)
      await(repo.removeJourneyData(testWrongJourneyId, testWrongJourneyId)) mustBe false
      await(repo.getJourneyData(testJourneyId, testInternalId)) mustBe Some(json)
    }
  }
  "repository" should {
    "have the correct TTL" in {
      repo.indexes.head.getOptions.getExpireAfter(TimeUnit.SECONDS) mustBe 86400
    }
  }
}
