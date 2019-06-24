add_library(lame-mp3-utils SHARED lame-mp3-utils.cpp ${SRC_LIST})

add_library(lame SHARED IMPORTED)
set_target_properties(lame PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/liblame.so")

find_library(log-lib log)

target_link_libraries(lame-mp3-utils lame ${log-lib})