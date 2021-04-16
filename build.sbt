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

scalaVersion := "2.13.5"

ThisBuild / versionScheme := Some("early-semver")
ThisBuild / version := "1.2.2"

ThisBuild / organization := "de.skress"

lazy val root = (project in file("."))
  .settings(
    publishMavenStyle := true
  )

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json"    % "2.9.2",
  "org.specs2"        %% "specs2-core"  % "4.10.6" % "test",
  "org.specs2"        %% "specs2-junit" % "4.10.6" % "test"
)
