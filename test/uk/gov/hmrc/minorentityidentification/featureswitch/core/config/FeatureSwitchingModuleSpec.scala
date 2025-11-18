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

package uk.gov.hmrc.minorentityidentification.featureswitch.core.config

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import uk.gov.hmrc.minorentityidentification.featureswitch.core.models.FeatureSwitchSetting

class FeatureSwitchingModuleSpec extends AnyWordSpec with Matchers {

  object TestFeatureSwitchingModule extends FeatureSwitchingModule()

  "FeatureSwitchingModule" should {

    "contain the feature switches DesStub and StubGetCtReference" in {

      TestFeatureSwitchingModule.switches mustBe Seq(DesStub, StubGetCtReference)

    }

    "be able to access the feature switch DesStub" in {

      TestFeatureSwitchingModule("feature-switch.des-stub") mustBe DesStub

    }

    "be able to access the feature switch StubGetCtReference" in {

      TestFeatureSwitchingModule("feature-switch.ct-reference-stub") mustBe StubGetCtReference

    }

    "raise an IllegalArgumentException when asked to access an unknown feature switch" in {

      try {
        TestFeatureSwitchingModule("unknown")
        fail("FeatureSwitchingModule must throw an IllegalArgumentException if the feature switch is not known")
      } catch {
        case _: IllegalArgumentException => succeed
      }

    }

    "write to JSON correctly" in {
      val setting = FeatureSwitchSetting("testConfig", "Test Feature", isEnabled = true)

      val json = Json.toJson(setting)

      (json \ "configName").as[String] mustBe "testConfig"
      (json \ "displayName").as[String] mustBe "Test Feature"
      (json \ "isEnabled").as[Boolean] mustBe true
    }

    "read from JSON correctly" in {
      val json = Json.obj(
        "configName"  -> "testConfig",
        "displayName" -> "Test Feature",
        "isEnabled"   -> false
      )

      val setting = json.as[FeatureSwitchSetting]

      setting.configName mustBe "testConfig"
      setting.displayName mustBe "Test Feature"
      setting.isEnabled mustBe false
    }

  }

}
