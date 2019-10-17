/*
 * Copyright 2019 Soeren Kress
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

name := "webpack-assets"

baseVersion in ThisBuild := "1.1.0"

organization in ThisBuild := "de.skress"
publishGithubUser in ThisBuild := "skress"
publishFullName in ThisBuild := "Soeren Kress"
startYear in ThisBuild := Some(2019)
strictSemVer in ThisBuild := false

bintrayVcsUrl in Global := Some("git@github.com:skress/webpack-assets.git")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json"    % "2.8.0-M6",
  "org.specs2"        %% "specs2-core"  % "4.8.0" % "test",
  "org.specs2"        %% "specs2-junit" % "4.8.0" % "test"
)
