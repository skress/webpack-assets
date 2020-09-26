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

import java.util.concurrent.atomic.AtomicReference

import scala.collection.concurrent.TrieMap

trait WebpackAssets {

  def of(name: BundleName): Map[AssetType, collection.Seq[AssetFilename]]

  def of(name: BundleName, assetType: AssetType): collection.Seq[AssetFilename]

}

class ImmutableWebpackAssets(private val entries: Map[BundleName, Map[AssetType, collection.Seq[AssetFilename]]]) extends WebpackAssets {

  def of(name: BundleName): Map[AssetType, collection.Seq[AssetFilename]] = {
    entries.getOrElse(name.toLowerCase, Map.empty)
  }

  def of(name: BundleName, assetType: AssetType): collection.Seq[AssetFilename] = {
    of(name.toLowerCase).getOrElse(assetType.toLowerCase, Seq.empty)
  }

}

class MutableWebpackAssets extends WebpackAssets {

  private val assets: AtomicReference[WebpackAssets] =
    new AtomicReference[WebpackAssets]()

  def of(name: BundleName): Map[AssetType, collection.Seq[AssetFilename]] =
    Option(assets.get).map(_.of(name)).getOrElse(Map.empty)

  def of(name: BundleName, assetType: AssetType): collection.Seq[AssetFilename] =
    Option(assets.get).map(_.of(name, assetType)).getOrElse(Seq.empty)

  /**
    * Reads the webpack assets manifest from the given file and updates the internal WebpackAssets representation.
    *
    * The parser will first try to load the assets manifest from the classpath. If that does not succeed it tries
    * to load the file from the filesystem.
    *
    * @param manifest filename of webpack assets file to parse
    */
  def updateFrom(manifest: WebpackManifestFile): Unit = {
    assets.set(new ImmutableWebpackAssets(new WebpackAssetsParser().parse(manifest)))
  }
}

object DefaultWebpackAssets extends WebpackAssets {

  private val assets = new MutableWebpackAssets()

  override def of(name: BundleName): Map[AssetType, collection.Seq[AssetFilename]] =
    assets.of(name)

  override def of(name: BundleName, assetType: AssetType): collection.Seq[AssetFilename] =
    assets.of(name, assetType)

  def updateFrom(manifest: WebpackManifestFile): Unit = assets.updateFrom(manifest)
}

object MultiWebpackAssets {

  private val multiAssets = new TrieMap[String, MutableWebpackAssets]()

  def of(alias: String, name: BundleName): Map[AssetType, collection.Seq[AssetFilename]] =
    multiAssets.get(alias).map(_.of(name)).getOrElse(Map.empty)

  def of(alias: String, name: BundleName, assetType: AssetType): collection.Seq[AssetFilename] =
    multiAssets.get(alias).map(_.of(name, assetType)).getOrElse(Seq.empty)

  /**
    * Updates the assets from the given input filename `manifest` and stores it under the given `alias`.
    *
    * @param manifest filename of the assets file
    * @param alias key which needs to be used to retrieve the assets (i.e. use this alias when using the `of` methods)
    */
  def updateFrom(manifest: WebpackManifestFile, alias: String): Unit = {
    val assets = new MutableWebpackAssets()
    assets.updateFrom(manifest)
    multiAssets.update(alias, assets)
  }
}
