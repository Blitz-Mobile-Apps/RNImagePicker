import { NativeModules } from 'react-native';
import { ImagePickerOptions } from 'rn-image-picker/types';

const { imagePicker } = NativeModules;

const open = async (options: ImagePickerOptions) => {
    return new Promise((resolve, reject) => {
        if (imagePicker) {
            if (imagePicker?.openImagePicker) {
                try {
                    imagePicker.openImagePicker(options, response => {
                        console.log(response);
                        resolve(response)
                    })

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