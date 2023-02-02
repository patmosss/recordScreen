package recordSCR.pojo;

/**
 * @author: tree
 * @description:
 * @date: 2023/2/2
 */
public class BaseInfo {

    private String outputFile;      //输出文件/地址(可以是本地文件，也可以是流媒体服务器地址)
    private int audio_device_index; //音频设备，本机默认是4
    private int framerate;          //视频帧率:最低 25(即每秒25张图片,低于25就会出现闪屏)
    private AudioType isOpenAudio;  //是否打开声音 2（立体声）；1（单声道）；0（无音频）
    private String screen_device_index; //设备号 0：本机设备 1：其它设备

    public BaseInfo() {
    }

    public BaseInfo(String outputFile, int audio_device_index, int framerate, AudioType isOpenAudio, String screen_device_index) {
        this.outputFile = outputFile;
        this.audio_device_index = audio_device_index;
        this.framerate = framerate;
        this.isOpenAudio = isOpenAudio;
        this.screen_device_index = screen_device_index;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public int getAudio_device_index() {
        return audio_device_index;
    }

    public void setAudio_device_index(int audio_device_index) {
        this.audio_device_index = audio_device_index;
    }

    public int getFramerate() {
        return framerate;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public AudioType getIsOpenAudio() {
        return isOpenAudio;
    }

    public void setIsOpenAudio(AudioType isOpenAudio) {
        this.isOpenAudio = isOpenAudio;
    }

    public String getScreen_device_index() {
        return screen_device_index;
    }

    public void setScreen_device_index(String screen_device_index) {
        this.screen_device_index = screen_device_index;
    }
}
