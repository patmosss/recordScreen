package recordSCR.pojo;

/**
 * @author: tree
 * @description:
 * @date: 2023/2/2
 */
public enum AudioType {
    STEREO(2),   //立体声
    Mono(1),     //单声道
    NOAUDIO(0);  //无音频

    int v;

    AudioType(int v) {
        this.v = v;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }
}
