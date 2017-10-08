package de.codecentric.boot.admin.management;

import com.google.common.io.Files;
import de.codecentric.boot.admin.config.OsCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppManagementUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppManagementUtil.class);

    public String getStatusOfPid(String pid) {
        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        String output = "";
        switch (ostype) {
            case Windows:
                break;
            case MacOS:
                break;
            case Linux:
                String command = "kill -0 " + pid;
                LOGGER.debug("Executed command is" + command);
                output = executeCommand(command);
                LOGGER.debug("Linux Command Executed" + output);

                break;
            case Other:
                break;
        }
        return output;
    }

    public ArrayList getFileNameListWithoutExtn(String folderLocation) {

        List<String> registeredAppList = null;
        try {
            registeredAppList = Files.readLines(new File("registered-apps.txt"), StandardCharsets.UTF_8);
            for (String registeredApp : registeredAppList) {
                String[] data = registeredApp.split("\\|");
                SSHConnectionManager.getAppsOverSSH(data[1], "ls " + folderLocation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList fileArrayList = new ArrayList();
        String fileName;
        File folder = new File(folderLocation);
        File[] listOfFiles = folder.listFiles();

        String strName = "name";
        String[] strArray = new String[]{strName};


        LOGGER.debug("The Path::" + folderLocation + "has" + listOfFiles.length + "Files");
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileName = listOfFiles[i].getName();
                System.out.println("File " + fileName);
                fileArrayList.add(Files.getNameWithoutExtension(fileName));
            }
        }
        return fileArrayList;
    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            //command = "ps -ef | grep 9877";
            //command = "kill -0 962";
            p = Runtime.getRuntime().exec(command);
            p.waitFor();

            if (p.exitValue() == 0) {
                output.append("SUCCESS");
            } else {
                output.append("FAILURE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public void getSSHClientDetails() {
    }
}
