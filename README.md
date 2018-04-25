# Mp3Converter
Use latest [Lame-3.100](http://lame.sourceforge.net/)  to transform PCM, WAV, AIFF and other uncompressed formats into MP3 format files.
# Usage
## Dependencies

```
//Project build.gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
//Module build.gradle
dependencies {
	        implementation 'com.github.Jay-Goo:Mp3Converter:v0.0.3'
	}
```
## Methods

```
 /**
     * init lame
     * @param inSampleRate
     *              input sample rate in Hz
     * @param channel
     *              number of channels
     * @param mode
     *              0 = CBR, 1 = VBR, 2 = ABR.  default = 0
     * @param outSampleRate
     *              output sample rate in Hz
     * @param outBitRate
     *              rate compression ratio in KHz
     * @param quality
     *              quality=0..9. 0=best (very slow). 9=worst.<br />
     *              recommended:<br />
     *              2 near-best quality, not too slow<br />
     *              5 good quality, fast<br />
     *              7 ok quality, really fast
     */
 Mp3Converter.init(44100, 1, 0, 44100, 96, 7);

 /**
     * file convert to mp3
     * it may cost a lot of time and better put it in a thread
     * @param input
     *          file path to be converted
     * @param mp3
     *          mp3 output file path
     */
    Mp3Converter.convertMp3(inputPath, mp3Path);


  /**
     * get converted bytes in inputBuffer
     * @return
     *          converted bytes in inputBuffer
     *          to ignore the deviation of the file size,when return to -1 represents convert complete
     */
	Mp3Converter.getConvertBytes()
```
## Build
You can use Android Studio `Make Module 'library'`to create your *.so files, they will be created in your `library/src/jniLibs`Folder.

# ABI
This library support `armeabi-v7a`, `arm64-v8a`, `mips`, `mips64`, `x86`, `x86_64`

# Blog
You can learn how to build this library through [this article](https://gujinjie.top/2018/04/19/%E6%89%8B%E6%8A%8A%E6%89%8B%E6%95%99%E4%BD%A0Android%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8NDK%E5%AE%9E%E7%8E%B0%E4%B8%80%E4%B8%AAMP3%E8%BD%AC%E7%A0%81%E5%BA%93/).

# Future
Support amr format

# License
MIT License
Copyright (c) 2018 Jay-Goo
