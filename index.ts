import { NativeModules, Platform } from 'react-native';
import { ImagePickerOptions } from 'rn-image-picker/types';

const { imagePicker } = NativeModules;

const open = async (options: ImagePickerOptions) => {
    return new Promise((resolve, reject) => {
        if (imagePicker) {
            if (imagePicker?.openImagePicker) {
                try {
                    if (Platform.OS == "android") {
                        imagePicker.openImagePicker(options, response => {
                            console.log(response);
                            resolve(response)
                        })


                    } else if (Platform.OS == "ios") {
                        let response = imagePicker.openImagePicker(options)
                        resolve(response)
                    }

                    // return resolve(response)
                } catch (e) {
                    reject(e)

                    // return Promise.reject(e)
                }
            }
        }
    })




}

export default {
    open: open
};