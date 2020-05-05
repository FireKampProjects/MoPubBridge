#import "MopubPlugin.h"
#if __has_include(<mopub/mopub-Swift.h>)
#import <mopub/mopub-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "mopub-Swift.h"
#endif

@implementation MopubPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMopubPlugin registerWithRegistrar:registrar];
}
@end
