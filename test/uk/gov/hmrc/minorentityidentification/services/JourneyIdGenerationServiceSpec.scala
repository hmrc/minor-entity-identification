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

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class JourneyIdGenerationServiceSpec extends AnyWordSpec with Matchers {

  "generateJourneyId" should {
    "return a non-empty string" in {
      val service = new JourneyIdGenerationService()
      val journeyId = service.generateJourneyId()

      journeyId must not be empty
      journeyId.length mustBe 36
    }

    "return a valid format" in {
      val service = new JourneyIdGenerationService()
      val journeyId = service.generateJourneyId()

      noException should be thrownBy java.util.UUID.fromString(journeyId)
    }

    "generate different IDs on consecutive calls" in {
      val service = new JourneyIdGenerationService()
      val id1 = service.generateJourneyId()
      val id2 = service.generateJourneyId()

      id1 must not equal id2
    }
  }
}