//
// Created by victor on 12.11.17.
//

#ifndef NATIVECODER_H
#define NATIVECODER_H

#include <malloc.h>
#include <time.h>
#include <fcntl.h>
#include <pthread.h>

#include <media/NdkMediaMuxer.h>
#include <media/NdkMediaCodec.h>
#include <media/NdkMediaFormat.h>

#include "Arguments.h"
#include "ThreadQueue.h"

#define SAMPLE_RATE (48000)
#define AUDIO_BIT_RATE (128000)
#define CHANNEL_COUNT (2)
#define CHANNEL_CONFIG (12)
#define AAC_PROFILE (2)
#define AUDIO_FORMAT (2)
#define AUDIO_SOURCE (1)
#define I_FRAME_INTERVAL (1)
#define AUDIO_MIME "audio/mp4a-latm"
#define VIDEO_MIME "video/avc"

class NativeCoder {
private:
    bool startFlag;
    bool isRecording;
    int videoTrack = -1;
    int audioTrack = -1;
    int64_t nanoTime;
    Arguments *arguments;

    int64_t fpsTime;

    ThreadQueue<void *> frame_queue;
    //AMediaMuxer mixer;
    AMediaCodec *videoCodec;
    AMediaCodec *audioCodec;
    pthread_mutex_t media_mutex;
public:
    NativeCoder(Arguments *arguments);

    bool init();

    static void *audioEncode(void *obj);

    static void *videoEncode(void *obj);
};


#endif
