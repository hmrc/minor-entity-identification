/*
 * Copyright 2021 HM Revenue & Customs
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

import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "5.16.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.68.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % "5.16.0" % "test, it",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-28" % "0.68.0" % "test, it",
    "org.mockito" %% "mockito-scala" % "1.16.42" % Test,
    "org.mockito" %% "mockito-scala-scalatest" % "1.16.42" % Test,
    "com.github.tomakehurst" % "wiremock-jre8-standalone" % "2.28.0" % "it"
  )
}
