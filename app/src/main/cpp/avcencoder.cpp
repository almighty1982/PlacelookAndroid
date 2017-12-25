#include <stdlib.h>
#include "avcencoder.h"

int64_t systemnanoTime() {
    timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    return now.tv_sec * 1000000000LL + now.tv_nsec;
}

NativeAVCEncoder::NativeAVCEncoder(const char *aMime, int aWidth, int aHeight, int aFrameRate,
                                   int aBitRate, int aColorFormat, int aIFrame) {
    strcpy(this->mime, aMime);
    this->width = aWidth;
    this->height = aHeight;
    this->frameRate = aFrameRate;
    this->bitRate = aBitRate;
    this->colorFormat = aColorFormat;
    this->iFrame = aIFrame;
}

NativeAVCEncoder *
NativeAVCEncoder::create(const char *aMime, int aWidth, int aHeight, int aFrameRate,
                         int aBitRate, int aColorFormat, int aIFrame) {
    return new NativeAVCEncoder(aMime, aWidth, aHeight, aFrameRate, aBitRate, aColorFormat,
                                aIFrame);
}

bool NativeAVCEncoder::open() {
    codec = AMediaCodec_createEncoderByType(this->mime);
    format = AMediaFormat_new();
    AMediaFormat_setString(this->format, AMEDIAFORMAT_KEY_MIME, this->mime);
    AMediaFormat_setInt32(this->format, AMEDIAFORMAT_KEY_WIDTH, this->width);
    AMediaFormat_setInt32(this->format, AMEDIAFORMAT_KEY_HEIGHT, this->height);
    AMediaFormat_setInt32(this->format, AMEDIAFORMAT_KEY_FRAME_RATE, this->frameRate);
    AMediaFormat_setInt32(this->format, AMEDIAFORMAT_KEY_BIT_RATE, this->bitRate);
    AMediaFormat_setInt32(this->format, AMEDIAFORMAT_KEY_COLOR_FORMAT, this->colorFormat);
    AMediaFormat_setInt32(this->format, AMEDIAFORMAT_KEY_I_FRAME_INTERVAL, this->iFrame);
    uint8_t sps[2] = {0x12, 0x12};
    uint8_t pps[2] = {0x12, 0x12};
    AMediaFormat_setBuffer(this->format, "csd-0", sps, 2);
    AMediaFormat_setBuffer(this->format, "csd-1", pps, 2);
    media_status_t status = AMediaCodec_configure(this->codec, this->format, NULL, NULL,
                                                  AMEDIACODEC_CONFIGURE_FLAG_ENCODE);
    return (status == AMEDIA_OK);
}

bool NativeAVCEncoder::start() {
    nano = systemnanoTime();
    pthread_mutex_lock(&media_mutex);
    media_status_t status = AMediaCodec_start(this->codec);
    pthread_mutex_unlock(&media_mutex);
    return (AMEDIA_OK == status);
}

size_t NativeAVCEncoder::encode(uint8_t *frame, uint8_t *result) {
    size_t size = 0;
    while (this->isStarted()) {
        if (frame == NULL) break;
        ssize_t index = AMediaCodec_dequeueInputBuffer(this->codec, -1);
        size_t out_size;
        if (index >= 0) {
            uint8_t *buffer = AMediaCodec_getInputBuffer(this->codec, index, &out_size);
            if (frame != NULL && out_size > 0) {
                memcpy(buffer, frame, out_size);
                AMediaCodec_queueInputBuffer(this->codec, index, 0, out_size,
                                             systemnanoTime() - nano / 1000, isStarted() ? 0
                                                                                         : AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM);

            }
            AMediaCodecBufferInfo *info = (AMediaCodecBufferInfo *) malloc(
                    sizeof(AMediaCodecBufferInfo));
            ssize_t outIndex;
            do {
                outIndex = AMediaCodec_dequeueOutputBuffer(this->codec, info, 0);
                size_t out_size;
                if (outIndex >= 0) {
                    uint8_t *outBuffer = AMediaCodec_getOutputBuffer(this->codec, outIndex,
                                                                     &out_size);
                    result = (uint8_t *) realloc(result, size + out_size);
                    memcpy(result + size, outBuffer, out_size);
                    size += out_size;
                    AMediaCodec_releaseOutputBuffer(this->codec, outIndex, false);
                    if (this->isStarted()) continue;
                } else if (outIndex == AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED) {
                    //TODO
                }
            } while (outIndex >= 0);
        }
    }
    return size;
}

bool NativeAVCEncoder::stop() {
    if (codec != NULL) AMediaCodec_stop(codec);
    return true;
}

bool NativeAVCEncoder::close() {
    if (codec != NULL) {
        AMediaCodec_delete(codec);
        codec = NULL;
    }
    return true;
}
