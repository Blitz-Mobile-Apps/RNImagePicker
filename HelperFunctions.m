//
//  HelperFunctions.m
//  CLImageEditor
//
//  Created by Ayesh on 22/09/2022.
//
#import "HelperFunctions.h"
#import "UIImage+ImageCompress.h"

@implementation HelperFunctions 

+ (UIImage *)resizeImage:(UIImage *)image
{
    float actualHeight = image.size.height;
    float actualWidth = image.size.width;
    float maxHeight = 300.0;
    float maxWidth = 400.0;
    float imgRatio = actualWidth/actualHeight;
    float maxRatio = maxWidth/maxHeight;
    float compressionQuality = 0.5;//50 percent compression
    
    if (actualHeight > maxHeight || actualWidth > maxWidth)
    {
        if(imgRatio < maxRatio)
        {
            //adjust width according to maxHeight
            imgRatio = maxHeight / actualHeight;
            actualWidth = imgRatio * actualWidth;
            actualHeight = maxHeight;
        }
        else if(imgRatio > maxRatio)
        {
            //adjust height according to maxWidth
            imgRatio = maxWidth / actualWidth;
            actualHeight = imgRatio * actualHeight;
            actualWidth = maxWidth;
        }
        else
        {
            actualHeight = maxHeight;
            actualWidth = maxWidth;
        }
    }
    
    CGRect rect = CGRectMake(0.0, 0.0, actualWidth, actualHeight);
    UIGraphicsBeginImageContext(rect.size);
    [image drawInRect:rect];
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    NSData *imageData = UIImageJPEGRepresentation(img, compressionQuality);
    UIGraphicsEndImageContext();
    
    return [UIImage imageWithData:imageData];
    
}

+ (NSString *)encodeToBase64String:(UIImage *)image {
    return [UIImagePNGRepresentation(image) base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
}

+ (NSMutableArray *)compressImages: (NSMutableArray *)images compressionRatio:(float)compressionRatio{
    NSMutableArray *imagesArray = [[NSMutableArray alloc ]init];
    for(int i = 0; i< images.count;i++){
        [imagesArray addObject:[UIImage compressImage:images[i][UIImagePickerControllerOriginalImage] compressRatio:compressionRatio]];
    }
    return imagesArray;
}

+ (NSDictionary *) generateImageObject: (UIImage *)image includeBase64:(BOOL)includeBase64 {
    
    NSString * imageBase64 = @"";
    
    if(includeBase64 == YES){
        imageBase64 = [self encodeToBase64String:(image)];
    }else{
        imageBase64= NULL;
    }
    
    NSData *imageData = UIImagePNGRepresentation(image);
    NSString* paths = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory,NSUserDomainMask,YES) lastObject];
    NSMutableString* aString = [NSMutableString stringWithFormat:@"cached%@", [[NSUUID UUID] UUIDString]];
    NSString *imagePath =[paths stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.png",aString]];
    
    if (![imageData writeToFile:imagePath atomically:NO]){
        return (@{@"error": @"error"});
    }else{
        if(includeBase64){
            return (@{@"base64": imageBase64,@"uri": imagePath});
        }else{
            return (@{@"uri": imagePath});
        }
    }
}

+ (NSDictionary *) generateImagesArray: (NSMutableArray<UIImage *> *)imagesArray includeBase64:(BOOL)includeBase64 {
    
    
    NSMutableArray * imageObjectsArray = [[NSMutableArray alloc] init];
    
    for(int i= 0; i< imagesArray.count; i++){
        NSString * imageBase64 = @"";
        if(includeBase64 == YES){
            imageBase64 = [self encodeToBase64String:(imagesArray[i])];
        }else{
            imageBase64= NULL;
        }
        
        NSData *imageData = UIImagePNGRepresentation(imagesArray[i]);
        NSString* paths = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory,NSUserDomainMask,YES) lastObject];
        NSMutableString* aString = [NSMutableString stringWithFormat:@"cached%@", [[NSUUID UUID] UUIDString]];
        NSString *imagePath =[paths stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.png",aString]];
        
        if (![imageData writeToFile:imagePath atomically:NO]){
            return (@{@"error": @"error"});
        }else{
            if(includeBase64){
                [imageObjectsArray addObject:@{@"base64": imageBase64,@"uri": imagePath}];
                
            }else{
                [imageObjectsArray addObject:@{@"uri": imagePath}];
            }
        }
        
    }
    if (imageObjectsArray.count == 0){
        return (@{@"error": @"error"});
    }else{
        return (@{@"data":imageObjectsArray});
    }
}



@end
