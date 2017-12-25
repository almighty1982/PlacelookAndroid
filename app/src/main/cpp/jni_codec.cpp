#include <jni.h>
#include "avcencoder.h"

extern "C" {


NativeAVCEncoder *codec;

JNIEXPORT jobject JNICALL
Java_com_placelook_camera_AVCEncoder_nativeCreate(JNIEnv *env, jclass type, jstring mime,
                                                  jint width, jint height, jint frameRate,
                                                  jint bitRate, jint colorFormat, jint iFrame) {
    jboolean isCopy;
    const char *chMime = env->GetStringUTFChars(mime, &isCopy);
    codec = NativeAVCEncoder::create(chMime, width, height, frameRate, bitRate, colorFormat,
                                     iFrame);
}

JNIEXPORT jboolean JNICALL
Java_com_placelook_camera_AVCEncoder_nativeOpen(JNIEnv *env, jclass type) {
    return codec->open();
}

JNIEXPORT jboolean JNICALL
Java_com_placelook_camera_AVCEncoder_nativeStart(JNIEnv *env, jclass type) {
    return codec->start();
}
JNIEXPORT jint JNICALL
Java_com_placelook_camera_AVCEncoder_nativeEncode(JNIEnv *env, jclass type, jbyteArray input,
                                                  jbyteArray output) {
}
JNIEXPORT jboolean JNICALL
Java_com_placelook_camera_AVCEncoder_nativeStop(JNIEnv *env, jclass type) {
    return codec->stop();
}
JNIEXPORT jboolean JNICALL
Java_com_placelook_camera_AVCEncoder_nativeClose(JNIEnv *env, jclass type) {
    return codec->close();
}


}