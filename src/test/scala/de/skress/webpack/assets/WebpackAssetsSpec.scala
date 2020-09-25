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

class WebpackAssetsSpec extends org.specs2.mutable.Specification {

  val assets = new ImmutableWebpackAssets(new WebpackAssetsParser().parse("/webpack-assets.json"))

  "When parsing the example assets definition" >> {
    "main bundle must have 4 js assets" >> {
      assets.of("main", "js") must contain(exactly(
        "/assets/javascripts/runtime.bundle.js",
        "/assets/javascripts/vendors~main~signIn~signUp.bundle.js",
        "/assets/javascripts/main~signIn~signUp.bundle.js",
        "/assets/javascripts/main.bundle.js"
      ))
    }
    "signIn bundle must have 1 js asset" >> {
      assets.of("signIn", "js") must contain(exactly("/assets/javascripts/signIn.bundle.js"))
    }
    "assets can be accessed by lower and upper case letters" >> {
      val lowerCase = assets.of("main", "js")
      val upperCase = assets.of("main", "JS")
      lowerCase.length must be_>(0)
      lowerCase must_== upperCase
    }
    "signIn bundle must have no css assets" >> {
      assets.of("signIn", "ss").length must_== 0
    }
  }
}
