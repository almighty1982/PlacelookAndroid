#include <stdlib.h>
#include <jni.h>
#include "NativeCoder.h"

NativeCoder *coder;

extern "C" {
jboolean Java_com_placelook_camera_nativeInit(JNIEnv *env, jclass type, jstring _path,
                                              jint width,
                                              jint height, jint frameRate, jint colorFormat,
                                              jint bitRate) {
    Arguments *arguments = (Arguments *) malloc(sizeof(Arguments));
    arguments->jniEnv = env;
    arguments->jniEnv->GetJavaVM(&arguments->javaVM);

    jclass AudioRecordClass = arguments->jniEnv->FindClass("android/media/AudioRecord");
    jmethodID init_id = arguments->jniEnv->GetMethodID(AudioRecordClass, "<init>", "(IIIII)V");
    jmethodID minBufferSize_id = arguments->jniEnv->GetStaticMethodID(AudioRecordClass,
                                                                      "getMinBufferSize", "(III)I");
    arguments->startRecording_id = arguments->jniEnv->GetMethodID(AudioRecordClass,
                                                                  "startRecording",
                                                                  "()V");
    arguments->read_id = arguments->jniEnv->GetMethodID(AudioRecordClass, "read", "([BII)I");
    arguments->stop_id = arguments->jniEnv->GetMethodID(AudioRecordClass, "stop", "()V");
    arguments->release_id = arguments->jniEnv->GetMethodID(AudioRecordClass, "release", "()V");

    jint bufferSize =
            arguments->jniEnv->CallStaticIntMethod(AudioRecordClass, minBufferSize_id, SAMPLE_RATE,
                                                   CHANNEL_CONFIG,
                                                   AUDIO_FORMAT) * CHANNEL_COUNT;
    jobject AudioRecordObject = arguments->jniEnv->NewObject(AudioRecordClass, init_id,
                                                             AUDIO_SOURCE, SAMPLE_RATE,
                                                             CHANNEL_CONFIG,
                                                             AUDIO_FORMAT, bufferSize);
    //当方法返回后AudioRecordObject对象将被回收，所以需要NewGlobalRef方法重新new一个全局对象，再调用DeleteGlobalRef销毁
    arguments->mAudioRecord = arguments->jniEnv->NewGlobalRef(AudioRecordObject);

    const char *path = env->GetStringUTFChars(_path, NULL);
    arguments->path = path;
    arguments->width = width;
    arguments->height = height;
    arguments->frameRate = frameRate;
    arguments->colorFormat = colorFormat;
    arguments->bitRate = bitRate;

    coder = new NativeCoder(arguments);
    if (coder->init()) {
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

}