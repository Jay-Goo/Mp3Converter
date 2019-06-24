#add cpp files into library
#  SUBDIRECTORY method
add_library(
        # Sets the name of the library.
        lame
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        lame.c
        bitstream.c
        encoder.c
        fft.c
        gain_analysis.c
        id3tag.c
        mpglib_interface.c
        newmdct.c
        presets.c
        psymodel.c
        quantize.c
        quantize_pvt.c
        reservoir.c
        set_get.c
        tables.c
        takehiro.c
        vbrquantize.c
        util.c
        VbrTag.c
        version.c
)

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        lame
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})