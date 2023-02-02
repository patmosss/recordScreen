package recordSCR.pojo;


public class ScreenInfo {

    int ScrHeight;
    int ScrWidth;


    public ScreenInfo() {
    }

    public ScreenInfo(int scrHeight, int scrWidth) {
        ScrHeight = scrHeight;
        ScrWidth = scrWidth;
    }

    public int getScrHeight() {
        return ScrHeight;
    }

    public void setScrHeight(int scrHeight) {
        ScrHeight = scrHeight;
    }

    public int getScrWidth() {
        return ScrWidth;
    }

    public void setScrWidth(int scrWidth) {
        ScrWidth = scrWidth;
    }
}
