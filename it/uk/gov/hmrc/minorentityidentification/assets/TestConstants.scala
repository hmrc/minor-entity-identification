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
}
