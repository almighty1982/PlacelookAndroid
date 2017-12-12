//
// Created by victor on 12.11.17.
//

#include "NativeCoder.h"
#include "Log.h"
#include <cstring>

int64_t systemnanotime() {
    timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    return now.tv_sec * 1000000000LL + now.tv_nsec;
}

NativeCoder::NativeCoder(Arguments *arguments) :
        arguments(arguments) {
    fpsTime = 1000 / arguments->frameRate;
    pthread_mutex_init(&media_mutex, NULL);
}

bool NativeCoder::init() {
    AMediaFormat *audioFormat = AMediaFormat_new();
    AMediaFormat_setString(audioFormat, AMEDIAFORMAT_KEY_MIME, AUDIO_MIME);
    AMediaFormat_setInt32(audioFormat, AMEDIAFORMAT_KEY_SAMPLE_RATE, SAMPLE_RATE);
    AMediaFormat_setInt32(audioFormat, AMEDIAFORMAT_KEY_AAC_PROFILE, AAC_PROFILE);
    AMediaFormat_setInt32(audioFormat, AMEDIAFORMAT_KEY_BIT_RATE, AUDIO_BIT_RATE);
    AMediaFormat_setInt32(audioFormat, AMEDIAFORMAT_KEY_CHANNEL_COUNT, CHANNEL_COUNT);
    AMediaFormat_setInt32(audioFormat, AMEDIAFORMAT_KEY_IS_ADTS, 0);
    uint8_t es[2] = {0x12, 0x12};
    AMediaFormat_setBuffer(audioFormat, "csd-0", es, 2);
    audioCodec = AMediaCodec_createEncoderByType(AUDIO_MIME);
    media_status_t audioConfigureStatus = AMediaCodec_configure(audioCodec, audioFormat, NULL, NULL,
                                                                AMEDIACODEC_CONFIGURE_FLAG_ENCODE);
    if (AMEDIA_OK != audioConfigureStatus) {
        LOGE("set audio configure failed status-->%d", audioConfigureStatus);
        return false;
    }
    AMediaFormat_delete(audioFormat);
    LOGI("init audioCodec success");


    AMediaFormat *videoFormat = AMediaFormat_new();
    AMediaFormat_setString(videoFormat, AMEDIAFORMAT_KEY_MIME, VIDEO_MIME);
    AMediaFormat_setInt32(videoFormat, AMEDIAFORMAT_KEY_WIDTH, arguments->width);
    AMediaFormat_setInt32(videoFormat, AMEDIAFORMAT_KEY_HEIGHT, arguments->height);
    AMediaFormat_setInt32(videoFormat, AMEDIAFORMAT_KEY_BIT_RATE, arguments->bitRate);
    AMediaFormat_setInt32(videoFormat, AMEDIAFORMAT_KEY_FRAME_RATE, arguments->frameRate);
    AMediaFormat_setInt32(videoFormat, AMEDIAFORMAT_KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);
    AMediaFormat_setInt32(videoFormat, AMEDIAFORMAT_KEY_COLOR_FORMAT, arguments->colorFormat);
    uint8_t sps[2] = {0x12, 0x12};
    uint8_t pps[2] = {0x12, 0x12};
    AMediaFormat_setBuffer(videoFormat, "csd-0", sps, 2); // sps
    AMediaFormat_setBuffer(videoFormat, "csd-1", pps, 2); // pps
    videoCodec = AMediaCodec_createEncoderByType(VIDEO_MIME);
    media_status_t videoConfigureStatus = AMediaCodec_configure(videoCodec, videoFormat, NULL, NULL,
                                                                AMEDIACODEC_CONFIGURE_FLAG_ENCODE);
    if (AMEDIA_OK != videoConfigureStatus) {
        LOGE("set video configure failed status-->%d", videoConfigureStatus);
        return false;
    }
    AMediaFormat_delete(videoFormat);
    LOGI("init videoCodec success");
}

void *NativeCoder::videoEncode(void *obj) {
    LOGI("Encode video");
    NativeCoder *record = (NativeCoder *) obj;
    while (record->startFlag) {
        if (record->frame_queue.empty()) continue;
        ssize_t index = AMediaCodec_dequeueInputBuffer(record->videoCodec, -1);
        size_t out_size;
        if (index >= 0) {
            uint8_t *buffer = AMediaCodec_getInputBuffer(record->videoCodec, index, &out_size);
            void *data = *record->frame_queue.wait_and_pop().get();
            if (data != NULL && out_size > 0) {
                memcpy(buffer, data, out_size);
                AMediaCodec_queueInputBuffer(record->videoCodec, index, 0, out_size,
                                             (systemnanotime() - record->nanoTime) / 1000,
                                             record->isRecording ? 0
                                                                 : AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM);
            }
        }
        AMediaCodecBufferInfo *info = (AMediaCodecBufferInfo *) malloc(
                sizeof(AMediaCodecBufferInfo));
        ssize_t outIndex;
        do {
            outIndex = AMediaCodec_dequeueOutputBuffer(record->videoCodec, info, 0);
            size_t out_size;
            if (outIndex >= 0) {
                uint8_t *outputBuffer = AMediaCodec_getOutputBuffer(record->videoCodec, outIndex,
                                                                    &out_size);
                if (record->audioTrack >= 0 && record->videoTrack >= 0 && info->size > 0 &&
                    info->presentationTimeUs > 0) {
                    //AMediaMuxer_writeSampleData(record->mixer, record->videoTrack, outputBuffer, info);
                }

                AMediaCodec_releaseOutputBuffer(record->videoCodec, outIndex, false);
                if (record->isRecording) {
                    continue;
                }
            } else if (outIndex == AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED) {
                AMediaFormat *outFormat = AMediaCodec_getOutputFormat(record->videoCodec);
                //ssize_t track = AMediaMuxer_addTrack(record->mixer, outFormat);
                const char *s = AMediaFormat_toString(outFormat);
                record->videoTrack = 0;
                LOGI("video out format %s", s);
                //LOGE("add video track status-->%d", track);
                //if (record->audioTrack >= 0 && record->videoTrack >= 0) {
                //    AMediaMuxer_start(record->mixer);
                //}
            }
        } while (outIndex >= 0);
    }
    return 0;
}

void *NativeCoder::audioEncode(void *obj) {

}

