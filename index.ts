
import { Alert, InteractionManager, NativeModules, Platform } from 'react-native';
import { ImagePickerOptions } from 'rn-image-picker/types';
const { imagePicker } = NativeModules;



const open = async (options: ImagePickerOptions) => {

    return new Promise(async (resolve, reject) => {

        try {

            if (!imagePicker || !imagePicker?.openImagePicker) throw new Error('Something went wrong')

            if (Platform.OS == "android") {

                imagePicker.openImagePicker(options, response => {

                    resolve(response)

                })

            } else {

                let response = await imagePicker.openImagePicker(options)

                resolve(response)

            }

        } catch (error) {

            reject(error)

        }

    })

}

export const openPicker = async (options: ImagePickerOptions) => {

    return new Promise((resolve, reject) => {

        Alert.alert('Image Picker', 'Please select your desired source', [

            {
                text: 'Cancel',
                style: 'destructive',
                onPress: () => InteractionManager.runAfterInteractions(() => {
                    reject('User cancelled')
                })

            },

            {

                text: 'Camera',
                onPress: () => InteractionManager.runAfterInteractions(async () => {
                    try {
                        const response = await open({ ...options, selection: 'camera' })
                        resolve(response)
                    } catch (error) {
                        reject(error)

                    }
                })
            },

            {
                text: 'Gallery',
                onPress: () => InteractionManager.runAfterInteractions(async () => {
                    try {
                        const response = await open({ ...options, selection: 'gallery' })
                        resolve(response)
                    } catch (error) {
                        reject(error)
                    }
                })
            },
        ], {
            cancelable: false,
        })

    })

}

export default {

    open: open,

    openPicker: openPicker

};