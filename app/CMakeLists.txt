cmake_minimum_required(VERSION 3.4.1)

add_library(
             NativeCoder
             SHARED
             src/main/cpp/JNI_NativeCoder.cpp
             src/main/cpp/NativeCoder.cpp
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