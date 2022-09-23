#import "imagePicker.h"







#import <React/RCTConvert.h>

#import "CLImageEditor.h"
#import "ELCImagePickerController.h"
#import "UIImage+ImageCompress.h"
#import "HelperFunctions.h"


@interface UIViewController()

<CLImageEditorDelegate, ELCImagePickerControllerDelegate>

@end



@implementation imagePicker



//extern NSMutableDictionary *imagePickedData;

extern NSString *imageBase64 = @"";

extern bool *sendImage = true;

extern NSString *imagePickedWithoutCompression = @"";

extern NSString *imageURI = @"";

extern NSInteger *count = 0;
extern RCTPromiseResolveBlock globalResolve = nil;
extern RCTPromiseRejectBlock globalReject = nil;

//options
extern NSString *selection = @"";
extern BOOL *includeBase64  = false;
extern BOOL *selectMultiple  = false;
extern int selectionLimit = 2;
extern float compressionRatio = 1;

NSMutableArray * imagesArray ;

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

RCT_EXPORT_METHOD(openImagePicker:(NSDictionary *)options resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)

{
    
    //  NSLog(@"Recieved Type: %@",notificationData);
    
    selection = [options valueForKey:@"selection"];
    includeBase64 = [[options valueForKey:@"includeBase64" ]boolValue];
    selectMultiple = [[options valueForKey:@"selectMultiple" ]boolValue];
    selectionLimit = [[options valueForKey:@"selectionLimit"] intValue];
    compressionRatio = [[options valueForKey:@"compressionRatio"] floatValue];
    imageURI = @"";
    
    sendImage = true;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        UIApplication *application = UIApplication.sharedApplication;
        
        UIViewController *rootViewController = RCTPresentedViewController();
        
        picker = [[UIImagePickerController alloc] init];
        
        picker.delegate = self;
        
        picker.allowsEditing = YES;
        
        globalResolve = resolve;
        globalReject = reject;
        
        
        
        if([selection isEqual:@"camera"])
        {
            CGSize screenSize = [[UIScreen mainScreen] bounds].size;
            picker.sourceType = UIImagePickerControllerSourceTypeCamera;
            picker.showsCameraControls = YES;
            float cameraAspectRatio = 4.0 / 3.0;
            float imageWidth = floorf(screenSize.width * cameraAspectRatio);
            float scale = ceilf((screenSize.height / imageWidth) * 10.0) / 10.0;
            picker.cameraViewTransform = CGAffineTransformMakeScale(scale, scale);
            [rootViewController presentViewController:picker animated:YES completion:nil];
        }else if ([selection isEqual:@"gallery"]){
            if(selectMultiple == YES){
                imagesArray = [[NSMutableArray alloc] init];
                ELCImagePickerController *imagePicker = [[ELCImagePickerController alloc] init];
                imagePicker.maximumImagesCount = selectionLimit; //Set the maximum number of images to select, defaults to 4
                imagePicker.imagePickerDelegate = self;
                
                [rootViewController presentViewController:imagePicker animated:YES completion:nil];
            }else{
                picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
                [rootViewController presentViewController:picker animated:YES completion:nil];
            }
            
        }else{
            
        }
    });
    
}


#pragma mark- Multiple Images Picker delegate

- (void)elcImagePickerController:(ELCImagePickerController *)picker didFinishPickingMediaWithInfo:(NSArray *)info{
    [imagesArray addObjectsFromArray: info];
    imagesArray = [HelperFunctions compressImages:imagesArray compressionRatio: compressionRatio];
    NSDictionary* imageObjectsArray = [[HelperFunctions generateImagesArray:imagesArray includeBase64:includeBase64] valueForKey:@"data"];
    globalResolve(imageObjectsArray);
    
    [picker dismissViewControllerAnimated:YES completion:nil];
}

- (void)elcImagePickerControllerDidCancel:(ELCImagePickerController *)picker{
    
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


#pragma mark- Single Image Picker delegate

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
    
    long imageDataSize = CGImageGetHeight(chosenImage.CGImage) * CGImageGetBytesPerRow(chosenImage.CGImage)/ 1024 / 1024;
    
    CLImageEditor *editor = [[CLImageEditor alloc] initWithImage:chosenImage];
    
    editor.delegate = self;
    
    CLImageToolInfo *tool;
    
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLFilterTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultVignetteFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultEmptyFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultLinearFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultInstantFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultProcessFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultTransferFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultSepiaFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultChromeFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultFadeFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultCurveFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultTonalFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultNoirFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultMonoFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDefaultInvertFilter" recursive:YES];
    //
    //    tool.available = NO;
    //
    //
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLAdjustmentTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLEffectTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLDrawTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLSplashTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLBlurTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLStickerTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLToneCurveTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLEmoticonTool" recursive:YES];
    //
    //    tool.available = NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLResizeTool" recursive:YES];
    //
    //    tool.available=NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLRotateTool" recursive:YES];
    //
    //    tool.available=NO;
    //
    //    tool = [editor.toolInfo subToolInfoWithToolName:@"CLTextTool" recursive:NO];
    //
    //    tool.available = NO;
    
    
    
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




#pragma mark- CLImageEditor delegate

- (void)imageEditor:(CLImageEditor *)editor didFinishEdittingWithImage:(UIImage *)image

{
    NSDictionary* imageObject = [HelperFunctions generateImageObject:image includeBase64:includeBase64];
    
    if([[[imageObject valueForKey:@"error"] stringValue] isEqualToString:@"error"]){
        if(globalReject != nil){
            NSError *e =  nil;
            globalReject(@"message", @"Failed to cache image data to disk",e);
            globalReject = nil;
        }
    }else{
        if(globalResolve != nil){
            globalResolve(imageObject);
            globalResolve = nil;
        }
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        
        UIApplication *application = UIApplication.sharedApplication;
        UIViewController *rootViewController = RCTPresentedViewController();
        [rootViewController dismissViewControllerAnimated:YES completion:nil];
        
    });
    
    editor = NULL;
    
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

@end
