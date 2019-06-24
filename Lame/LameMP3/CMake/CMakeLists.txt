# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

#define cpp source path
#set(SRC_DIR src/main/cpp/lamemp3)

#set *.h source path
include_directories(src/main/cpp/lamemp3)

#set cpp source path
#aux_source_directory(src/main/cpp/lamemp3 SRC_LIST)

#set *.so files output path，please add this before add_library
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
set(jnilibs "${CMAKE_SOURCE_DIR}/src/main/jniLibs")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${jnilibs}/${ANDROID_ABI})

ADD_SUBDIRECTORY(${CMAKE_SOURCE_DIR}/src/main/cpp/lamemp3)