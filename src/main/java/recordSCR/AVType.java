package recordSCR;


public enum AVType {
    Tune("tune"),
    Constant_Rate_Factor("crf"),
    Flv("flv"),
    Preset("preset");

    String key;

    AVType(String key) {
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
