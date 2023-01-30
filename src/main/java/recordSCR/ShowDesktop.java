package recordSCR;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ShowDesktop {
    public static void main(String[] args) throws Exception{
        RecordSCR recordSCR = new RecordSCR();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        recordSCR.recordingScreen("D:\\Temp\\" + date + UUID.randomUUID() +".flv",
                4,
                25, "1");

    }
}
