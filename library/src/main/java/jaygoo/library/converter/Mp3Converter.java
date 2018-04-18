package jaygoo.library.converter;

/**
 * ================================================
 * 作    者：JayGoo
 * 版    本：
 * 创建日期：2018/4/18
 * 描    述:use lame to convert file to mp3
 * ================================================
 */
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
