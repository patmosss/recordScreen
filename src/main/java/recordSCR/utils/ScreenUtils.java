package recordSCR.utils;

import recordSCR.pojo.ScreenInfo;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 屏幕工具类
 */
public class ScreenUtils {

    public static HashMap<String, ScreenInfo> getSCRInfo() {

        HashMap<String, ScreenInfo> scrInfoMap = new HashMap<>();

        GraphicsEnvironment localGrapEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices = localGrapEnv.getScreenDevices();
        for (GraphicsDevice device : devices) {
            String id = Pattern.compile("[^0-9]").matcher(device.getIDstring()).replaceAll("").trim();
            ScreenInfo si = new ScreenInfo(device.getDisplayMode().getHeight(), device.getDisplayMode().getWidth());
            scrInfoMap.put(id, si);
        }
        return scrInfoMap;
    }

    public static void main(String[] args) {
        HashMap<String, ScreenInfo> scrInfo = getSCRInfo();
        for (Map.Entry<String, ScreenInfo> stringScreenInfoEntry : scrInfo.entrySet()) {
            System.out.println(stringScreenInfoEntry.getKey() + stringScreenInfoEntry.getValue().getScrWidth() + stringScreenInfoEntry.getValue().getScrHeight());
        }
    }
}
