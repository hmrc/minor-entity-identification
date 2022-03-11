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

package uk.gov.hmrc.minorentityidentification.connectors

import play.api.http.Status.OK
import play.api.libs.json._
import uk.gov.hmrc.http._
import uk.gov.hmrc.minorentityidentification.config.AppConfig
import uk.gov.hmrc.minorentityidentification.connectors.RegisterWithMultipleIdentifiersHttpParser.{RegisterWithMultipleIdentifiersHttpReads, RegisterWithMultipleIdentifiersResult}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegisterWithMultipleIdentifiersConnector @Inject()(http: HttpClient,
                                                         appConfig: AppConfig
                                                        )(implicit ec: ExecutionContext) {
  lazy val extraHeaders = Seq(
    "Authorization" -> appConfig.desAuthorisationToken,
    "Environment" -> appConfig.desEnvironmentHeader,
    "Content-Type" -> "application/json"
  )

  implicit val httpReads: HttpReads[RegisterWithMultipleIdentifiersResult] = RegisterWithMultipleIdentifiersHttpReads

  def register(jsonBody: JsObject, regime: String)(implicit hc: HeaderCarrier): Future[RegisterWithMultipleIdentifiersResult] = {
    http.POST[JsObject, RegisterWithMultipleIdentifiersResult](
      url = appConfig.getRegisterWithMultipleIdentifiersUrl(regime),
      headers = extraHeaders,
      body = jsonBody
    )
  }
}

object RegisterWithMultipleIdentifiersHttpParser {

  val IdentificationKey = "identification"
  val IdentificationTypeKey = "idType"
  val IdentificationValueKey = "idValue"
  val SafeIdKey = "SAFEID"

  object RegisterWithMultipleIdentifiersHttpReads extends HttpReads[RegisterWithMultipleIdentifiersResult] {

    override def read(method: String, url: String, response: HttpResponse): RegisterWithMultipleIdentifiersResult = {
      response.status match {
        case OK =>
          (for {
            idType <- (response.json \ IdentificationKey \ 0 \ IdentificationTypeKey).validate[String]
            if idType == SafeIdKey
            safeId <- (response.json \ IdentificationKey \ 0 \ IdentificationValueKey).validate[String]
          } yield safeId) match {
            case JsSuccess(safeId, _) => RegisterWithMultipleIdentifiersSuccess(safeId)
            case _: JsError => throw new InternalServerException(s"Invalid JSON returned on Register API: ${response.body}")
          }
        case _ =>
          throw new InternalServerException(
            s"Invalid response on Register API with status: ${response.status}, body: ${response.body} and headers: ${response.headers}"
          )
      }
    }
  }

  sealed trait RegisterWithMultipleIdentifiersResult

  case class RegisterWithMultipleIdentifiersSuccess(safeId: String) extends RegisterWithMultipleIdentifiersResult
}
