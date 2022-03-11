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

package uk.gov.hmrc.minorentityidentification.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.minorentityidentification.connectors.RegisterWithMultipleIdentifiersHttpParser.RegisterWithMultipleIdentifiersSuccess
import uk.gov.hmrc.minorentityidentification.services.RegisterWithMultipleIdentifiersService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RegisterBusinessEntityController @Inject()(cc: ControllerComponents,
                                                 registerWithMultipleIdentifiersService: RegisterWithMultipleIdentifiersService,
                                                 val authConnector: AuthConnector
                                                )(implicit ec: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  def registerTrust(): Action[(String, String)] = Action.async(parse.json[(String, String)](json => for {
    sautr <- (json \ "sautr").validate[String]
    regime <- (json \ "regime").validate[String]
  } yield (sautr, regime))) {
    implicit request =>
      authorised() {
        val (sautr, regime) = request.body
        registerWithMultipleIdentifiersService.registerTrust(sautr, regime).map {
          case RegisterWithMultipleIdentifiersSuccess(safeId) =>
            Ok(Json.obj(
              "registration" -> Json.obj(
                "registrationStatus" -> "REGISTERED",
                "registeredBusinessPartnerId" -> safeId)))
        }
      }
  }

  def registerUA(): Action[(String, String)] = Action.async(parse.json[(String, String)](json => for {
    ctutr <- (json \ "ctutr").validate[String]
    regime <- (json \ "regime").validate[String]
  } yield (ctutr, regime))) {
    implicit request =>
      authorised() {
        val (ctutr, regime) = request.body
        registerWithMultipleIdentifiersService.registerUA(ctutr, regime).map {
          case RegisterWithMultipleIdentifiersSuccess(safeId) =>
            Ok(Json.obj(
              "registration" -> Json.obj(
                "registrationStatus" -> "REGISTERED",
                "registeredBusinessPartnerId" -> safeId)))
        }
      }
  }
}