//
// Created by Administrator on 2017/8/15.
//

#ifndef ARGUMENTS_H
#define ARGUMENTS_H

#include <jni.h>

typedef struct Arguments {
    jobject mAudioRecord;
    jmethodID startRecording_id, read_id, stop_id, release_id;
    JavaVM *javaVM;
    JNIEnv *jniEnv;

    const char *path;
    int width, height, frameRate, colorFormat, bitRate;
};

#endif //ARGUMENTS_H
