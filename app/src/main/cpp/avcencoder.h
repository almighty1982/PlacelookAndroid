

#ifndef AVCENCODER_H
#define AVCENCODER_H

#include <jni.h>
#include <string.h>
#include <media/NdkMediaCodec.h>
#include <media/NdkMediaMuxer.h>
#include <pthread.h>
#include "ThreadQueue.cpp"

class NativeAVCEncoder {
private:
    char *mime;
    int width;
    int height;
    int frameRate;
    int bitRate;
    int colorFormat;
    int iFrame;

    AMediaCodec *codec;
    AMediaFormat *format;

    bool started;
    pthread_mutex_t media_mutex;

    int64_t nano;

    NativeAVCEncoder(const char *aMime, int aWidth, int aHeight, int aFrameRate, int aBitRate,
                     int aColorFormat, int aIFrame);

public:

    static NativeAVCEncoder *
    create(const char *aMime, int aWidth, int aHeight, int aFrameRate, int aBitRate,
           int aColorFormat, int aIFrame);

    bool open();

    bool start();

    size_t encode(uint8_t *frame, uint8_t *result);

    bool stop();

    bool close();

    bool isStarted() { return started; }
};

#endif //AVCENCODER_H