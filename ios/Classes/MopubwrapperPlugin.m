#import "MopubwrapperPlugin.h"
#if __has_include(<mopubwrapper/mopubwrapper-Swift.h>)
#import <mopubwrapper/mopubwrapper-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "mopubwrapper-Swift.h"
#endif

@implementation MopubwrapperPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMopubwrapperPlugin registerWithRegistrar:registrar];
}
@end
