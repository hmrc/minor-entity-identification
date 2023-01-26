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

package uk.gov.hmrc.minorentityidentification.connectors

import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse, InternalServerException}
import uk.gov.hmrc.minorentityidentification.config.AppConfig
import uk.gov.hmrc.minorentityidentification.connectors.GetCtReferenceHttpParser.GetCtReferenceHttpReads

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetCtReferenceConnector @Inject()(http: HttpClient,
                                        appConfig: AppConfig
                                       )(implicit ec: ExecutionContext) {

  def getCtReference(ctutr: String)(implicit hc: HeaderCarrier): Future[Option[String]] = {
    val extraHeaders = Seq(
      "Authorization" -> appConfig.desAuthorisationToken,
      "Environment" -> appConfig.desEnvironmentHeader
    )

    http.GET[Option[String]](appConfig.getCtReferenceUrl(ctutr), headers = extraHeaders)(GetCtReferenceHttpReads, hc, ec)
  }

}

object GetCtReferenceHttpParser {

  val postcodeKey = "companyPostCode"

  implicit object GetCtReferenceHttpReads extends HttpReads[Option[String]] {
    override def read(method: String, url: String, response: HttpResponse): Option[String] = {
      response.status match {
        case OK =>
          (response.json \ postcodeKey).validate[String] match {
            case JsSuccess(postcode, _) =>
              Some(postcode)
            case JsError(_) =>
              throw new InternalServerException(s"Get CT Reference returned malformed JSON")
          }
        case NOT_FOUND =>
          None
        case status =>
          throw new InternalServerException(s"Get CT Reference failed with status: $status, body: ${response.body} and headers: ${response.headers}")
      }
    }
  }
}
