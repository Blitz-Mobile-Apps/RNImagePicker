import { NativeModules, NativeEventEmitter } from 'react-native';

const { imagePicker } = NativeModules;
// var eventEmitter = new NativeEventEmitter(imagePicker);
const open = async (onSuccess, onError) => {
    if(imagePicker){
        if(imagePicker.openImagePicker){
            try{
            const response = await imagePicker.openImagePicker()
            onSuccess(response)
            return resolve(response)
            }catch(e){
                onError(e)
                return Promise.reject(e)
            }
        }
    }
    // if (!onSuccess) {
    //     console.warn('Please add onSuccess callback to image picker')
    //     return
    // }
    // if (!onError) {
    //     console.warn('Please add onError callback to image picker')
    //     return
    // }

    // let onSuccessEvent = eventEmitter.addListener('onSuccess', (event) => {
    //     onSuccess(event)
    //     removeListener()

    // })
    // let onErrorEvent = eventEmitter.addListener('onError', (event) => {
    //     onError(event)
    //     removeListener()
    // })
    // imagePicker.openImagePicker()
    // const removeListener = () => {
    //     eventEmitter.removeSubscription(onErrorEvent)
    //     eventEmitter.removeSubscription(onSuccessEvent)
    // }
}

export default {
    open: open
};