#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint mopubwrapper.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'mopubwrapper'
  s.version          = '0.0.3'
  s.summary          = 'A new Flutter plugin.'
  s.description      = <<-DESC
A new Flutter plugin.
                       DESC
  s.homepage         = 'https://github.com/manikandan-selvanathan/MoPubBridge.git'
  s.license          = 'MIT'
  s.author           = { 'Manikandan Selvanathan' => 'manikandan.selvanathan.ca@gmail.com' }
  s.source       = { :git => 'https://github.com/manikandan-selvanathan/MoPubBridge', :branch => 'master' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.dependency 'mopub-ios-sdk/Core'
  s.dependency 'MoPub-UnityAds-Adapters'
  s.dependency 'MoPub-IronSource-Adapters'
  s.dependency 'MoPub-Vungle-Adapters'
  s.dependency 'MoPub-Applovin-Adapters'
  s.dependency 'MoPub-FacebookAudienceNetwork-Adapters'
  s.dependency 'MoPub-AdMob-Adapters'
  s.dependency 'MoPub-AdColony-Adapters'
  s.platform = :ios, '9.0'
  s.static_framework = true

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
  s.swift_version = '5.0'
end
