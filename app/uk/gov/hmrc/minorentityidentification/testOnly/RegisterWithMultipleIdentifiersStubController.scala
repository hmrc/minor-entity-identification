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

import play.api.libs.json.{JsError, JsObject, JsSuccess, Json, Reads}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, InternalServerException}
import uk.gov.hmrc.minorentityidentification.config.AppConfig
import uk.gov.hmrc.minorentityidentification.services.JourneyDataService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegisterWithMultipleIdentifiersStubController @Inject()(controllerComponents: ControllerComponents,
                                                              journeyDataService: JourneyDataService,
                                                              val authConnector: AuthConnector
                                                             )(implicit ec: ExecutionContext) extends BackendController(controllerComponents) with AuthorisedFunctions {

  private val stubbedSafeId: String = "X00000123456789"

  private def successfulResponseAsJson(safeID: String): JsObject = {
    val responseAsString: String =
      s"""
         |{
         |  "identification" : [
         |    {
         |      "idType" : "SAFEID",
         |      "idValue" : "$safeID"
         |    }
         |  ]
         |}""".stripMargin

    Json.parse(responseAsString).as[JsObject]
  }

  val singleFailureResultAsString: String =
    """{
      |  "code" : "INVALID_PAYLOAD",
      |  "reason" : "Request has not passed validation. Invalid Payload."
      |}""".stripMargin

  val multipleFailureResultAsString: String =
    """
      |{
      |    "failures" : [
      |      {
      |        "code" : "INVALID_PAYLOAD",
      |        "reason" : "Request has not passed validation. Invalid Payload."
      |      },
      |      {
      |        "code" : "INVALID_REGIME",
      |        "reason" : "Request has not passed validation. Invalid Regime."
      |      }
      |    ]
      |}""".stripMargin

  val singleFailureResponseAsJson: JsObject = Json.parse(singleFailureResultAsString).as[JsObject]
  val multipleFailureResponseAsJson: JsObject = Json.parse(multipleFailureResultAsString).as[JsObject]

  def registerWithMultipleIdentifiers(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
          journeyDataService.getJourneyDataByKey(journeyId, "stubs", authInternalId).map {
            case Some(value) =>
              println("registrationJson: " + value)
              (value.as[JsObject] \ "registrationStub" \ "registrationStatus").validate[String] match {
                case JsSuccess("REGISTERED", _) =>
                  (value.as[JsObject] \ "registrationStub" \ "registeredBusinessPartnerId").validate[String] match {
                    case JsSuccess(safeId, _) => Ok(successfulResponseAsJson(safeId))
                    case JsError(_) => throw new InternalServerException("error")
                  }
                case JsSuccess("REGISTRATION_FAILED", _) => BadRequest(multipleFailureResponseAsJson)
                case JsError(_) =>  throw new InternalServerException("error")
              }
            case None => throw new InternalServerException("error")
          }
        case None => throw new InternalServerException("error")
      }
  }

  val registerWithMultipleIdentifiers: Action[(Option[String], Option[String])] = Action(parse.json[(Option[String], Option[String])](json => for {
    saUtr <- (json \ "trust" \ "sautr").validateOpt[String]
    ctUtr <- (json \ "unincorporatedAssociation" \ "ctutr").validateOpt[String]
  } yield (saUtr, ctUtr))) {
    implicit request =>

      val (optSaUtr, optCtUtr) = request.body

      (optSaUtr, optCtUtr) match {
        case (Some(saUtr), None) => createResponse(saUtr)
        case (None, Some(ctUtr)) => createResponse(ctUtr)
        case _ => throw new InternalServerException(s"Unexpected input to registration stub: SA Utr - $optSaUtr CT Utr - $optCtUtr")
      }
  }

  private def createResponse(identifier: String): Result = {
    identifier match {
      case "0000000001" => BadRequest(singleFailureResponseAsJson)
      case "0000000002" => BadRequest(multipleFailureResponseAsJson)
      case _ => Ok(successfulResponseAsJson(stubbedSafeId))
    }

  }

}
