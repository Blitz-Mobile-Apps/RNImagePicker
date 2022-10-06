//
//  HelperFunctions.h
//  Pods
//
//  Created by Ayesh on 22/09/2022.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface HelperFunctions : NSObject

+ (UIImage *)resizeImage:(UIImage *)image;
+ (NSString *)encodeToBase64String:(UIImage *)image;
+ (NSMutableArray *)compressImages: (NSMutableArray *)images compressionRatio:(float)compressionRatio;
+ (NSDictionary *) generateImageObject: (UIImage *)image includeBase64:(BOOL)includeBase64 compressionRatio:(float)compressionRatio;
+ (NSDictionary *) generateImagesArray: (NSMutableArray<UIImage *> *)imagesArray includeBase64:(BOOL)includeBase64;
@end

NS_ASSUME_NONNULL_END
