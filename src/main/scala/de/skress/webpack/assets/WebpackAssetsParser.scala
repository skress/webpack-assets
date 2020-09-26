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

package de.skress.webpack.assets

import java.io.{FileInputStream, InputStream}
import play.api.libs.json._
import scala.collection.Seq

class WebpackAssetsParser {

  /**
    * Parses the file either from the classpath or from the filesystem and returns a map that contains
    * an entry for each Webpack bundle. Each of these entries are maps that map from asset type (e.g. "js", "css")
    * to a sequence of asset filenames.
    *
    * @param manifest filename of webpack assets file to parse
    * @return parsed webpack assets file as a map
    */
  def parse(manifest: WebpackManifestFile): Map[BundleName, Map[AssetType, Seq[AssetFilename]]] = {
    // webpack assets plugin generates a JsObject ...
    val rootObject = Json.parse(getResource(manifest)).as[JsObject]

    val result =
      new collection.mutable.HashMap[BundleName, Map[AssetType, Seq[AssetFilename]]]()

    // ... which has the bundles as properties ...
    for ((bundleName, bundleAssets) <- rootObject.fields) {

      // ... for each bundle an object is generated ...
      val assets = bundleAssets.as[JsObject]
      val bundleResult =
        new collection.mutable.HashMap[AssetType, Seq[AssetFilename]]()

      // ... that has the file types (e.g. "JS", "CSS") as top elements
      for ((assetType, assetFiles) <- assets.fields) {
        // ... and for each file type there is either exactly one file name or an array of file names
        bundleResult += (
          (
            assetType.toLowerCase,
            assetFiles
              .asOpt[JsString]
              .map(s => Seq(s.value))
              .getOrElse(assetFiles.as[JsArray].value.map(_.as[JsString].value: AssetFilename))
          )
        )
      }
      result += ((bundleName.toLowerCase, bundleResult.toMap))
    }
    result.toMap
  }

  private def getResource(filename: String): InputStream = {
    Option(getClass.getResourceAsStream(filename)).getOrElse(new FileInputStream(filename))
  }
}
