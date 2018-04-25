通过本文你可以学到以下知识：

 - 如何实现一个Android MP3转码库
 - 一些和音频转码相关的基础知识
 - 如何使用NDK将c/c++项目移植到Android端，并使用Java调用c/c++代码
 - 如何使用CMake构建NDK项目
 - 如何生成不同CPU架构所需的动态链接库


# 工具简介
### Lame
 LAME 是最好的MP3编码器，速度快，效果好，特别是中高码率和VBR编码方面。
 
### NDK
原生开发工具包，即帮助开发原生代码的一系列工具，包括但不限于编译工具、一些公共库、开发IDE等。它提供了完整的一套将 c/c++ 代码编译成静态/动态库的工具，而 `Android.mk` 和 `Application.mk` 你可以认为是描述编译参数和一些配置的文件。比如指定使用c++11还是c++14编译，会引用哪些共享库，并描述关系等，还会指定编译的`abi`。只有有了这些 NDK 中的编译工具才能准确的编译 c/c++ 代码。

### CMake简介
`CMake`是一个跨平台的编译工具，它并不会直接编译出对象，而是根据自定义的语言规则（`CMakeLists.txt`）生成 对应 makefile 或 project 文件，然后再调用底层的编译。Android Studio 2.2以后开始支持`CMake`，所以现在我们有2种方式来编译c/c++ 代码。一个是 `ndk-build + Android.mk + Application.mk` 组合，另一个是 `CMake + CMakeLists.txt` 组合，它们都不会影响我们的android代码和c/c++代码，只是构建方式和结构不同。

`CMake`相对传统`ndk-build`的优点在于：无需手动生成Java的头文件、相对于mk文件配置更简单、可以自动生成对应`abi`的`*.so`动态链接库、支持设置断点调试（我认为这是最方便的地方）、可以引用其他已经生成的so库。
 
# 准备工作

 1. 在Android Studio 上安装好NDK和CMake，网上教程很多这里就不在赘述。
 2. 下载[Lame](http://lame.sourceforge.net/)源码。
 
# 项目结构
￼通过这张项目结构可以先帮助我们更形象整体的理解CMake构建NDK的方式。
![这里写图片描述](https://img-blog.csdn.net/20180419153555394?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE3NDc3ODE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

*Tips：如果你对CMake刚接触，可以先用Android Studio创建一个项目，然后勾选上`include c++`选项，去看下demo的结构，帮助理解，我就是这样做的，效果还不错。*

# Lame源码移植

 1. 首先在`src/main/`目录下新建一个`cpp`文件夹，我们可以将Lame源码中`libmp3lame`拷贝到`cpp`文件夹下，当然这里我们也可以重命名，例如我命名为`lamemp3`（以下介绍我将沿用此名）。
 2. 将Lame源码中的`include`文件夹下的`lame.h`复制到`lamemp3`文件夹中。
 3. 剔除`lamemp3`中不必要的文件和目录，只保留`.c`和`.h`文件，因为其他文件大多都是批处理文件，对于Android不是必需的。
 4. 修改`util.h`的源码。在570行找到`ieee754_float32_t`数据类型，将其修改为`float`类型，因为`ieee754_float32_t`是Linux或者是Unix下支持的数据类型，在Android下并不支持。
 5. `set_get.h`中24行将`include <lame.h>`改为`include "lame.h"`。
 6. 在`id3tag.c`和`machine.h`两个文件里，將`HAVE_STRCHR`和`HAVE_MEMCPY`的ifdef结构体注释掉，不然编译会报错。
 
```
#ifdef STDC_HEADERS
# include <stdlib.h>
# include <string.h>
#else
/*
# ifndef HAVE_STRCHR
#  define strchr index
#  define strrchr rindex
# endif
*/
char   *strchr(), *strrchr();
/*
# ifndef HAVE_MEMCPY
#  define memcpy(d, s, n) bcopy ((s), (d), (n))
#  define memmove(d, s, n) bcopy ((s), (d), (n))
# endif
*/
#endif
```
# CMakeLists编写
在`src`中新建一个名为`CMakeLists.txt`的文件（注意，这里的`CMakeLists.txt`不一定非要放到这里，只要它的位置和`build.gradle`文件的配置相对应就行）。

我们看下`CMakeLists.txt`的内容，这里我把注释已经写得很详细了，大家看下就很明白了：

```
# 指定CMake最低版本
cmake_minimum_required(VERSION 3.4.1)

# 定义常量
set(SRC_DIR main/cpp/lamemp3)

# 指定关联的头文件目录
include_directories(main/cpp/lamemp3)

# 查找在某个路径下的所有源文件
aux_source_directory(main/cpp/lamemp3 SRC_LIST)

# 设置 *.so 文件输出路径，要放在在add_library之前，不然不会起作用
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})

# 声明库名称、类型、源码文件
add_library(lame-mp3-utils SHARED main/cpp/lame-mp3-utils.cpp ${SRC_LIST})

# 定位某个NDK库，这里定位的是log库
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# 将NDK库链接到native库中，这样native库才能调用NDK库中的函数
target_link_libraries( # Specifies the target library.
                       lame-mp3-utils

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
```
# build.gradle配置

```
android {
    ......
    defaultConfig {
        ......
        externalNativeBuild {
            cmake {
                cppFlags ""
                abiFilters 'armeabi-v7a','arm64-v8a','mips','mips64','x86','x86_64' //要支持的abi
            }
        }
    }
    externalNativeBuild {
        cmake {
            path "src/CMakeLists.txt"//配置文件路径
        }
    }
}
```
# 编写Java native方法
这里我在代码中注释已经写得非常详细了，关于一些参数我会在下面做更详细的解释。
```
public class Mp3Converter {

    static  {
        System.loadLibrary("lame-mp3-utils");
    }

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
    public native static void init(int inSampleRate, int channel, int mode,
                                   int outSampleRate, int outBitRate, int quality);


    /**
     * file convert to mp3
     * it may cost a lot of time and better put it in a thread
     * @param input
     *          file path to be converted
     * @param mp3
     *          mp3 output file path
     */
    public native  static void convertMp3(String input, String mp3);


    /**
     * get converted bytes in inputBuffer
     * @return
     *          converted bytes in inputBuffer
     *          to ignore the deviation of the file size,when return to -1 represents convert complete
     */
    public native static long getConvertBytes();

    /**
     * get library lame version
     * @return
     */
    public native static String getLameVersion();

}
```

# 编写调用C/C++的cpp
先看一个上面Java文件中`native init(args...)` 方法在这里是如何实现的：

```
extern "C" JNIEXPORT void JNICALL
Java_jaygoo_library_converter_Mp3Converter_init(JNIEnv *env, jclass type, jint inSampleRate,
                                               jint channel, jint mode, jint outSampleRate,
                                               jint outBitRate, jint quality) {
    lameInit(inSampleRate, channel, mode, outSampleRate, outBitRate, quality);
}
```

 - `extern "C"`因为我们写的是cpp是c++文件，所以当我们调用一些c文件的方法时需要加上`extern "C"`，不然会提示找不到方法。
 - `Java_jaygoo_library_converter_Mp3Converter_init`这里方法名是和Java文件中的native方法一一对应的，这样才能让native方法找到对应的cpp方法。格式是：`Java_包名_类名_方法名`，这里包名的`.`用`_`代替，所以我们native的方法名命名尽量不要包含`_`，但如果真的包含了，那么在cpp文件中用`1`代替Java native 中的`_`。
 - `JNIEXPORT void JNICALL`是固定的格式，也是辅助native方法找到对应的cpp方法。
 - `JNIEnv *env`JNIEnv是指向JNINativeInterface结构的指针，当我们需要调用JNI方法时，都需要通过这个指针才能进行调用。
 
其实我们还可以通过Android Studio来自动生成这些方法和参数，在Android Studio中点击native方法名，快捷键`alt+enter`即可自动生成了。

看到这里，大家基本对如何编写cpp代码有一定的了解，接下来我来介绍下`lame-mp3-utils.cpp`的实现，由于篇幅有限，就不全上代码了，这里介绍几个比较关键的方法。


----------


## init
这里主要是对Lame进行一些初始化，主要的参数包括：

 1. inSampleRate 要转换的音频文件采样率
 2. mode 音频编码模式，包括VBR、ABR、CBR
 3. outSampleRate 转换后音频文件采样率
 4. outBitRate 输出的码率
 5. quality 压缩质量（具体数值上面注释已经写的很清楚了）

 
这里的代码没什么可看的，主要是调用一些lame自带的方法设置一些配置参数，最后调用`lame_init_params(lame)`完成初始化，这里我对上面几个参数出现的名词做下解释：
 
 - `采样率`每秒从连续信号中提取并组成离散信号的采样个数，单位Hz。数值越高，音质越好，常见的如8000Hz、11025Hz、22050Hz、32000Hz、44100Hz等。
 - `码率`又称比特率是指每秒传送的比特(bit)数，单位kbps，越高音质越好（相同编码格式下）。
 - `CBR`常数比特率编码，码率固定，速度较快，但压缩的文件相比其他模式较大，音质也不会有很大提高，适用于流式播放方案，lame默认的方案是这种。
 - `VBR`动态比特率编码，码率不固定。适用于下载后在本地播放或者在读取速度有限的设备播放，体积和为`CBR`的一半左右，但是输出码率不可控
 - `ABR`平均比特率编码，是Lame针对CBR不佳的文件体积比和VBR生成文件大小不定的特点独创的编码模式。是一种折中方案，码率基本可控，但是好像用的不多。
 
 ## convertMp3(jstring jInputPath, jstring jMp3Path)
 首先我们要将`jstring`转换为c++中的`char*`后才可以使用，我们可以通过JNI提供的`GetStringUTFChars`方法完成转换：
 

```
const char* cInput = env->GetStringUTFChars(jInputPath, 0);
const char* cMp3 = env->GetStringUTFChars(jMp3Path, 0);
```
然后我们通过`fopen`来打开需要操作的文件，用`rb`来读取输入文件，用`wb`来写转换后的文件。

```
 FILE* fInput = fopen(cInputPath,"rb");
 FILE* fMp3 = fopen(cMp3Path,"wb");
```
接下来我们申请两个buffer来缓存文件数据，我们边读边转换，然后再将转换后的数据写入文件。由于Lame的要求，这里的buffer数据必须要不小于7200，下面是具体的转换代码：

```
 //convert to mp3
    do{
        //这里将输入文件内容读取到inputBuffer中，当全部读取会返回0
        read = static_cast<int>(fread(inputBuffer, sizeof(short int) * 2, 8192, fInput));
        //这里用于计算读取的原文件的byte数，可以用于计算转换的进度
        total +=  read * sizeof(short int)*2;
        nowConvertBytes = total;
        if(read != 0){
            //这里用lame将inputBuffer转换为MP3格式的数据放入mp3Buffer中
            write = lame_encode_buffer_interleaved(lame, inputBuffer, read, mp3Buffer, BUFFER_SIZE);
            //将转换好的mp3Buffer的数据写入文件
            fwrite(mp3Buffer, sizeof(unsigned char), static_cast<size_t>(write), fMp3);
        }
        //最后全部读取完成后及时flush
        if(read == 0){
            lame_encode_flush(lame,mp3Buffer, BUFFER_SIZE);
        }
    }while(read != 0);
```
最后记得转换后释放资源：

```
    resetLame();
    fclose(fInput);
    fclose(fMp3);
    env->ReleaseStringUTFChars(jInputPath, cInput);
    env->ReleaseStringUTFChars(jMp3Path, cMp3);
```
# 生成不同ABI下的so库
为了支持不同的设备，我们需要根据不同的ABI生成不同的so库来调用，我们可以通过Android Studio的`Make`来调用`CMakeList.txt`脚本生成支持各种ABI版本的so库。文件输出路径可以通过配置`CMakeList.txt`来修改：

```
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})
```
其中`PROJECT_SOURCE_DIR`是指脚本所在目录，`ANDROID_ABI`是指在`build.gradle`中配置的`abiFilters`。

## ABI扩展知识
ABI（Application binary interface）应用程序二进制接口。不同的CPU 与指令集的每种组合都有定义的 ABI (应用程序二进制接口)，一段程序只有遵循这个接口规范才能在该 CPU 上运行，所以同样的程序代码为了兼容多个不同的CPU，需要为不同的 ABI 构建不同的库文件。当然对于CPU来说，不同的架构并不意味着一定互不兼容。

 - armeabi设备只兼容armeabi
 - armeabi-v7a设备兼容armeabi-v7a、armeabi
 - arm64-v8a设备兼容arm64-v8a、armeabi-v7a、armeabi
 - x86设备兼容X86、armeabi
 - mips64设备兼容mips64、mips
 - mips只兼容mips；
 
# 参考文献

 https://blog.csdn.net/allen315410/article/details/42456661
 
 https://www.jianshu.com/p/6332418b12b1
 

