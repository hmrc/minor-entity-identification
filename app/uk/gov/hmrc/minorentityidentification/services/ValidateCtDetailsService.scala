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

package uk.gov.hmrc.minorentityidentification.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.minorentityidentification.connectors.GetCtReferenceConnector
import uk.gov.hmrc.minorentityidentification.models.{CtutrValidationResult, DetailsMatched, DetailsMismatched, DetailsNotFound}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValidateCtDetailsService @Inject()(getCtReferenceConnector: GetCtReferenceConnector)(implicit ec: ExecutionContext) {

  def validateDetails(ctutr: String, postcode: String)(implicit hc: HeaderCarrier): Future[CtutrValidationResult] = {
    getCtReferenceConnector.getCtReference(ctutr).map {
      case Some(retrievedPostcode) if retrievedPostcode.filterNot(_.isWhitespace) equalsIgnoreCase postcode.filterNot(_.isWhitespace) =>
        DetailsMatched
      case Some(_) =>
        DetailsMismatched
      case None =>
        DetailsNotFound
    }
  }

}
