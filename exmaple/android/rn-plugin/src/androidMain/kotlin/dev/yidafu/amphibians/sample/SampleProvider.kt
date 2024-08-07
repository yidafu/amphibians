package dev.yidafu.amphibians.sample

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfoProvider
import kotlin.String

public class SampleProvider : TurboReactPackage() {
  override fun getModule(name: String, reactApplicationContext: ReactApplicationContext):
      NativeModule? = when(name) {
    SimpleModuleAndroid.NAME -> SimpleModuleAndroid(reactApplicationContext)else -> null
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider? = null
}
