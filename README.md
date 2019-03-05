# webpack-assets

Parses the output of [assets-webpack-plugin](https://www.npmjs.com/package/assets-webpack-plugin) and provides access to each bundle's assets.

## Usage

```sbt
resolvers += Resolver.bintrayRepo("skress", "maven")
libraryDependencies += "de.skress" %% "webpack-assets" % "<version>"
```

### Configure webpack

```javascript
const AssetsPlugin = require('assets-webpack-plugin');

module.exports = {
    // ...
    plugins: [new AssetsPlugin({
            entrypoints: true,
            prettyPrint: true,
            fullPath: true,
            path: '/path/to/the/scala/app/conf/folder'
        })]
}
```

When running webpack a file `webpack-assets.json` will be generated. See [assets-webpack-plugin homepage](https://www.npmjs.com/package/assets-webpack-plugin) for configuration options.

### Access generated assets from Scala

```scala
import de.skress.webpack.assets._
new ImmutableWebpackAssets(new WebpackAssetsParser().parse("/path/to/file/webpack-assets.json"))
```

### Usage with Play Framework

The code below assumes that the `AssetsPlugin` configuration's path is configured as follows (i.e. in dev mode `webpack-assets.json` is written to `/tmp` and in prod mode it is written to the app's `conf` folder.)

```javascript
path: mode === "development" ? path.resolve('/tmp/') : path.resolve(__dirname, '../../../conf/')
```

If the config would be written to the `conf` folder in dev mode as well, the app would be restarted after each change in the JavaScript part of the app. Instead a filewatcher is set up that updates the read assets without restarting the app.

Add this to your app's configuration:

```
webpack {
  assets {
    prod_path = /webpack-assets.json
    dev_path = /tmp/webpack-assets.json
  }
}
```

Now you can include the following code in one of your modules (the code uses [Schwatcher](https://github.com/lloydmeta/schwatcher), so you need to include it as a dependency in your `build.sbt`):

````scala
class Module(environment: Environment, configuration: Configuration) extends AbstractModule with ScalaModule with Logging {

  override def configure(): Unit = {
    bind(classOf[WebpackAssetsWatcher]).asEagerSingleton()
  }

}

...

class WebpackAssetsWatcher @Inject()(implicit system: ActorSystem,
                                     environment: Environment,
                                     configuration: Configuration)  {

  if (environment.mode == play.api.Mode.Prod) {
    DefaultWebpackAssets.updateFrom(configuration.get[String]("webpack.assets.prod_path"))
  } else {
    import com.beachape.filemanagement.MonitorActor
    import com.beachape.filemanagement.RegistryTypes._
    import com.beachape.filemanagement.Messages._
    import java.nio.file.Paths
    import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

    val assetsPath = configuration.get[String]("webpack.assets.dev_path")
    DefaultWebpackAssets.updateFrom(assetsPath)
    val fileMonitorActor = system.actorOf(MonitorActor(concurrency = 2))

    val modifyCallbackFile: Callback = { path => {
      Logger("webpack-assets-watcher").info("Updating webpack assets")
      DefaultWebpackAssets.updateFrom(assetsPath)
    }}
    val webpackAssets = Paths get assetsPath

    fileMonitorActor ! RegisterCallback(
      event = ENTRY_MODIFY,
      path = webpackAssets,
      callback =  modifyCallbackFile
    )
  }

}

````