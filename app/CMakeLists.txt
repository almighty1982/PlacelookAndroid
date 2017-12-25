cmake_minimum_required(VERSION 3.4.1)

add_library(
             NativeCoder
             SHARED
             src/main/cpp/avcencoder.cpp
             src/main/cpp/ThreadQueue.cpp
             src/main/cpp/jni_codec.cpp
              )

find_library(
              log-lib
              log )

target_link_libraries(
                       NativeCoder
                       android
                       mediandk
                       ${log-lib}
                      )