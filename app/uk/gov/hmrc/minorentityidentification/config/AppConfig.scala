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

package uk.gov.hmrc.minorentityidentification.config

import uk.gov.hmrc.minorentityidentification.featureswitch.core.config.{FeatureSwitching, DesStub}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig) extends FeatureSwitching {

  val authBaseUrl: String = servicesConfig.baseUrl("auth")

  val auditingEnabled: Boolean = servicesConfig.getBoolean("auditing.enabled")
  val graphiteHost: String = servicesConfig.getString("microservice.metrics.graphite.host")

  val timeToLiveSeconds: Long = servicesConfig.getInt("mongodb.timeToLiveSeconds").toLong

  lazy val desStubBaseUrl: String = servicesConfig.getString("microservice.services.des.stub-url")

  lazy val desBaseUrl: String = servicesConfig.getString("microservice.services.des.url")

  lazy val desAuthorisationToken: String =
    s"Bearer ${servicesConfig.getString("microservice.services.des.authorisation-token")}"

  lazy val desEnvironment: String =
    servicesConfig.getString("microservice.services.des.environment")

  def getRegisterWithMultipleIdentifiersUrl(regime: String): String = {
    val baseUrl = if (isEnabled(DesStub)) desStubBaseUrl else desBaseUrl
    s"$baseUrl/cross-regime/register/GRS?grsRegime=$regime"
  }
}
