#import "imagePicker.h"







#import <React/RCTConvert.h>

#import "CLImageEditor.h"

#import "UIImage+ImageCompress.h"



@interface UIViewController()

<CLImageEditorDelegate>

@end



@implementation imagePicker



//extern NSMutableDictionary *imagePickedData;

extern NSString *imagePicked = @"";

extern bool *sendImage = true;

extern NSString *imagePickedWithoutCompression = @"";

extern NSString *imageURI = @"";

extern NSInteger *count = 0;
extern RCTPromiseResolveBlock globalResolve = nil;
extern RCTPromiseRejectBlock globalReject = nil;


UIImagePickerController *picker;

RCT_EXPORT_MODULE(imagePicker);



+ (id)allocWithZone:(NSZone *)zone {
    
    static imagePicker *sharedInstance = nil;
    
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        
        sharedInstance = [super allocWithZone:zone];
        
    });
    
    return sharedInstance;
    
}



- (NSArray<NSString *> *)supportedEvents {
    
    return @[@"onSuccess",@"onError"];
    
}







RCT_EXPORT_METHOD(openImagePicker:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)

{
    
    //  NSLog(@"Recieved Type: %@",notificationData);
    
    imagePicked = @"";
    
    imageURI = @"";
    
    sendImage = true;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        UIApplication *application = UIApplication.sharedApplication;
        
        UIViewController *rootViewController = RCTPresentedViewController();
        
        picker = [[UIImagePickerController alloc] init];
        
        picker.delegate = self;
        
        picker.allowsEditing = YES;
        
        
        
        
        
        
        
        
        
        
        
        
        
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Image Source" message:@"Please select your desired image source" preferredStyle:UIAlertControllerStyleAlert];
        
        [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
            NSError *error = nil;
            reject(@"message", @"User cancelled",error);
            // Called when user taps outside
            
        }]];
        
        
        
        
        
        UIAlertAction *camera = [UIAlertAction actionWithTitle:@"Camera" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            
            //button click event
            
            globalResolve = resolve;
            globalReject = reject;
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleNotification:) name:@"_UIImagePickerControllerUserDidCaptureItem" object:nil ];
            
            CGSize screenSize = [[UIScreen mainScreen] bounds].size;
            
            picker.sourceType = UIImagePickerControllerSourceTypeCamera;
            
            
            
            picker.showsCameraControls = YES;
            
            //          cameraButton.frame = CGRect(x: 0, y: 0, width: 75, height: 75)
            
            //          cameraButton.backgroundColor = .clear
            
            //          cameraButton.layer.borderWidth = 6
            
            //          cameraButton.layer.borderColor = UIColor.white.cgColor
            
            //          cameraButton.layer.masksToBounds = true
            
            //          cameraButton.setTitleColor(UIColor.white, for: .normal)
            
            //          cameraButton.layer.cornerRadius = 75/2
            
            //          cameraButton.layer.position = CGPoint(x: self.view.frame.width/2, y:self.view.frame.height - 60)
            
            
            //
            //            UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
            //
            //            [button addTarget:self
            //
            //                       action:@selector(aMethod:)
            //
            //             forControlEvents:UIControlEventTouchUpInside];
            //
            //           button.frame = CGRectMake(screenSize.width/2-35, screenSize.height - 120, 70.0, 70.0);
            //
            //          button.layer.borderWidth = 6;
            //
            //          button.layer.borderColor = UIColor.whiteColor.CGColor;
            //
            //          button.layer.masksToBounds = true;
            //
            //          button.layer.cornerRadius = 70/2;
            //
            ////          [button setImage:<#(nullable UIImage *)#> forState:<#(UIControlState)#>:@"Tap" forState:UIControlStateNormal];
            //
            //
            //
            ////            [picker.cameraOverlayView addSubview:button];
            //
            //          picker.cameraOverlayView = button;
            
            // Device's screen size (ignoring rotation intentionally):
            
            
            
            
            
            // iOS is going to calculate a size which constrains the 4:3 aspect ratio
            
            // to the screen size. We're basically mimicking that here to determine
            
            // what size the system will likely display the image at on screen.
            
            // NOTE: screenSize.width may seem odd in this calculation - but, remember,
            
            // the devices only take 4:3 images when they are oriented *sideways*.
            
            float cameraAspectRatio = 4.0 / 3.0;
            
            float imageWidth = floorf(screenSize.width * cameraAspectRatio);
            
            float scale = ceilf((screenSize.height / imageWidth) * 10.0) / 10.0;
            
            
            
            picker.cameraViewTransform = CGAffineTransformMakeScale(scale, scale);
            
            //          [picker.view setFrame:CGRectMake(80.0, 210.0, 160.0, 40.0)]
            
            //          [picker ]
            
            //        [picker did]
            
            [rootViewController presentViewController:picker animated:YES completion:nil];
            
        }];
        
        
        
        UIAlertAction *library = [UIAlertAction actionWithTitle:@"Library" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            globalResolve = resolve;
            globalReject = reject;
            
            
            picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
            
            [rootViewController presentViewController:picker animated:YES completion:nil];
            
        }];
        
        
        
        [alert addAction:camera];
        
        [alert addAction:library];
        
        [rootViewController presentViewController:alert animated:YES completion:nil];
        
        //      [rootViewController presentingViewController:alert animated:YES co]
        
        //      [rootViewController presentingViewController]
        
        //      [rootViewController presentationController]
        
        
        
    });
    
}

-(void)aMethod:(UIButton *) sender {
    
    NSLog(@"Test button");
    
    [picker takePicture];
    
}

-(void)handleNotification:(NSNotification *)message {
    
    if ([[message name] isEqualToString:@"_UIImagePickerControllerUserDidCaptureItem"]) {
        
        // Remove overlay, so that it is not available on the preview view;
        
        //     [picker dismissViewControllerAnimated:YES completion:nil];
        
        //        [picker did]
        
        NSLog(@"Test NSnotification");
        
    }
    
    if ([[message name] isEqualToString:@"_UIImagePickerControllerUserDidRejectItem"]) {
        
        // Retake button pressed on preview. Add overlay, so that is available on the camera again
        
        //        picker.cameraOverlayView = [addCameraRollButton];
        
        picker.cameraOverlayView = nil;
        
    }
    
}



- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    if(globalReject != nil){
        NSError *e =  nil;
        globalReject(@"message", @"User cancelled the selection",e);
        globalReject = nil;
    }
    sendImage = false;
    
    [picker dismissViewControllerAnimated:YES completion:nil];
    
};



- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    
    
    UIImage *chosenImage = info[UIImagePickerControllerOriginalImage];
    
    imagePickedWithoutCompression = [self encodeToBase64String:(chosenImage)];
    
    NSLog(@"imageEditorimagePickerController: %@",chosenImage);
    
    //  imagePicked = [self encodeToBase64String:(chosenImage)];
    
    //  NSLog(@"imagePicked: %@",imagePicked);
    
    long imageDataSize = CGImageGetHeight(chosenImage.CGImage) * CGImageGetBytesPerRow(chosenImage.CGImage)/ 1024 / 1024;
    
    NSLog(@"imagePickedSize: %@",[NSString stringWithFormat:@"%li", imageDataSize]);
    
    
    
    
    
    CLImageEditor *editor = [[CLImageEditor alloc] initWithImage:chosenImage];
    
    
    
    editor.delegate = self;
    
    CLImageToolInfo *tool;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLFilterTool" recursive:YES];
    
    tool.available = NO;
    
    
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultVignetteFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultEmptyFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultLinearFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultInstantFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultProcessFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultTransferFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultSepiaFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultChromeFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultFadeFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultCurveFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultTonalFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultNoirFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultMonoFilter" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultInvertFilter" recursive:YES];
    
    tool.available = NO;
    
    
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLAdjustmentTool" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLEffectTool" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDrawTool" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLSplashTool" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLBlurTool" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLStickerTool" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLToneCurveTool" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLEmoticonTool" recursive:YES];
    
    tool.available = NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLResizeTool" recursive:YES];
    
    tool.available=NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLRotateTool" recursive:YES];
    
    tool.available=NO;
    
    tool = [editor.toolInfo subToolInfoWithToolName:@"CLTextTool" recursive:NO];
    
    tool.available = NO;
    
    
    
    //  [picker pushViewController:editor animated:YES];
    
    
    
    [picker dismissViewControllerAnimated:YES completion:nil];
    
    //  [picker presentViewController:editor animated:YES completion:nil];
    
    
    
    //    [picker dismissViewControllerAnimated:YES completion:nil];
    
    picker = NULL;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        UIApplication *application = UIApplication.sharedApplication;
        
        UIViewController *rootViewController = RCTPresentedViewController();
        
        [rootViewController presentViewController:editor animated:YES completion:nil];
        
    });
    
    
    
}









- (void)imageEditorDidCancel:(CLImageEditor *)editor

{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        UIApplication *application = UIApplication.sharedApplication;
        
        UIViewController *rootViewController = RCTPresentedViewController();
        if(globalReject != nil){
            NSError *e =  nil;
            globalReject(@"message", @"User cancelled the selection",e);
            globalReject = nil;
        }
        [rootViewController dismissViewControllerAnimated:YES completion:nil];
        
    });
    
}

#pragma mark- CLImageEditor delegate



- (void)imageEditor:(CLImageEditor *)editor didFinishEdittingWithImage:(UIImage *)image

{
    
    NSLog(@"imageEditor: %@",image);
    
    UIImage *compressedImage = [UIImage compressImage:image compressRatio:0.9f];
    
    imagePicked = [self encodeToBase64String:(compressedImage)];
    
    
    
    NSData *imageData = UIImagePNGRepresentation(compressedImage);
    
    NSString* paths = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory,
                                                           
                                                           NSUserDomainMask,
                                                           
                                                           YES) lastObject];
    
    NSMutableString* aString = [NSMutableString stringWithFormat:@"cached%d", count];    // does not need to be released. Needs to be retained if you need to keep use it after the current function.
    
    //  [aString appendFormat:@"... now has another int: %d", count];
    
    NSString *imagePath =[paths stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.png",aString]];
    
    count = count + 1;
    
    
    
    
    
    NSLog(@"pre writing to file");
    
    if (![imageData writeToFile:imagePath atomically:NO])
        
    {
        
//        [self sendEventWithName:@"onError" body:@{@"message": @"Failed to cache image data to disk"}];
        if(globalReject != nil){
            NSError *e =  nil;
            globalReject(@"message", @"Failed to cache image data to disk",e);
            globalReject = nil;
        }
        NSLog(@"Failed to cache image data to disk");
        
    }
    
    else
        
    {
        
        NSLog(@"the cachedImagedPath is %@",imagePath);
        
        imageURI = imagePath;
        if(globalResolve != nil){
            globalResolve(@{@"data": imagePicked,@"uri": imageURI});
            globalResolve = nil;
        }
//        [self sendEventWithName:@"onSuccess" body:@{@"data": imagePicked,@"uri": imageURI}];
        
    }
    
    
    
    long imageDataSize = CGImageGetHeight(image.CGImage) * CGImageGetBytesPerRow(image.CGImage) / 1024 / 1024;
    
    long compressedimageDataSize = CGImageGetHeight(compressedImage.CGImage) * CGImageGetBytesPerRow(compressedImage.CGImage) / 1024 / 1024;
    
    NSLog(@"imagePickedEdittedSize: %@",[NSString stringWithFormat:@"%li", imageDataSize]);
    
    NSLog(@"imagePickedEdittedCompressedSize: %@",[NSString stringWithFormat:@"%li", compressedimageDataSize]);
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        UIApplication *application = UIApplication.sharedApplication;
        
        UIViewController *rootViewController = RCTPresentedViewController();
        
        [rootViewController dismissViewControllerAnimated:YES completion:nil];
        
        
        
    });
    
    
    
    //  picker = NULL;
    
    editor = NULL;
    
    
    
}



- (NSString *)encodeToBase64String:(UIImage *)image {
    
    return [UIImagePNGRepresentation(image) base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
    
}

@end
