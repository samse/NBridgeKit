# NBridgeKit

NBridgeKit is Smiple Web-Native bridge library for android

Usage
-----

Step. 1
Add it in your settings.gradle at the end of repositories
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}

```

Step. 2
Add the dependency
```
dependencies {
  implementation 'com.github.samse:NBridgeKit:Tag'
}
```

Step. 3
Modify MainActivity extend to BaseActivity. and load url
```
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webWindow.loadUrl("https://mypage_url/sample.html")
    }
}
```

