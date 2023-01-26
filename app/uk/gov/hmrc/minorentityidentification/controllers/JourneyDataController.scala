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

package uk.gov.hmrc.minorentityidentification.controllers

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.minorentityidentification.services.JourneyDataService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class JourneyDataController @Inject()(cc: ControllerComponents,
                                      journeyDataService: JourneyDataService,
                                      val authConnector: AuthConnector)
                                     (implicit ec: ExecutionContext) extends BackendController(cc) with AuthorisedFunctions {

  def createJourney(): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(internalId) =>
          val journeyIdKey = "journeyId"
          journeyDataService.createJourney(internalId).map {
            journeyId => Created(Json.obj(journeyIdKey -> journeyId))
          }
        case None =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

  def getJourneyData(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(internalId) =>
          journeyDataService.getJourneyData(journeyId, internalId).map {
            case Some(journeyData) =>
              Ok(journeyData)
            case None =>
              NotFound(Json.obj(
                "code" -> "NOT_FOUND",
                "reason" -> s"No data exists for journey ID: $journeyId"
              ))
          }
        case None =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

  def getJourneyDataByKey(journeyId: String, dataKey: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(internalId) =>
          journeyDataService.getJourneyDataByKey(journeyId, dataKey, internalId).map {
            case Some(journeyData) => Ok(journeyData)
            case None => NotFound(Json.obj("code" -> "NOT_FOUND",
              "reason" -> s"No data exists for either journey ID: $journeyId or data key: $dataKey"))
          }
        case None =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

  def updateJourneyData(journeyId: String, dataKey: String): Action[JsValue] = Action.async(parse.json) {
    implicit req =>
      authorised().retrieve(internalId) {
        case Some(internalId) =>
          journeyDataService.updateJourneyData(journeyId, internalId, dataKey, req.body).map {
            hasUpdated =>
              if (hasUpdated)
                Ok
              else
                throw new InternalServerException(s"The provided field does not exist for $journeyId and could not be updated")
          }
        case None =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

  def removeJourneyDataField(journeyId: String, dataKey: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(internalId) =>
          journeyDataService.removeJourneyDataField(journeyId, internalId, dataKey).map {
            _ => NoContent
          }
        case None =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

  def removeJourneyData(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(internalId) =>
          journeyDataService.removeJourneyData(journeyId, internalId).map {
            _ => NoContent
          }
        case None =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

}
