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

import sbt.*

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % "8.5.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % "1.8.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % "8.5.0" % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % "1.8.0" % Test,
    "org.mockito" %% "mockito-scala" % "1.17.31" % Test,
    "org.mockito" %% "mockito-scala-scalatest" % "1.17.31" % Test,
  )
}
