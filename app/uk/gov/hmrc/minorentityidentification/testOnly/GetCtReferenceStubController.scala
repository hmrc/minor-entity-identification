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

package uk.gov.hmrc.minorentityidentification.testOnly

import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}

@Singleton
class GetCtReferenceStubController @Inject()(controllerComponents: ControllerComponents) extends BackendController(controllerComponents) {

  def getCtReference(ctUtr: String): Action[AnyContent] = Action {
    ctUtr match {
      case "0000000000" =>
        NotFound(
          Json.obj(
            "code" -> "NOT_FOUND",
            "reason" -> "The back end has indicated that CT UTR cannot be returned"
          )
        )
      case _ => Ok(
        Json.obj(
          fields = "companyName" -> "ACME",
          "companyAddress" -> "Address 1",
          "companyPostCode" -> "NE98 1ZZ"
        )
      )
    }
  }

}
