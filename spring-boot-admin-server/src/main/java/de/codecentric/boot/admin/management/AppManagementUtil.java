package de.codecentric.boot.admin.management;

import com.google.common.io.Files;
import de.codecentric.boot.admin.config.OsCheck;
import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.registry.ApplicationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class AppManagementUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppManagementUtil.class);

    @Autowired
    ApplicationRegistry applicationRegistry;

    private String appDataFileConstant = "registered-apps.txt";

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

    public ArrayList getFileNameListWithoutExtn(String folderLocation, String hostname, String hostPass, String fileExtn) {

        List<String> registeredAppList = null;
        List<String> listOfFilesArray = new ArrayList();
        ArrayList fileArrayList = new ArrayList();
        ArrayList isHostScannedForFiles = new ArrayList();
        File[] listOfFiles = null;
        String fileName = null;

        try {
            registeredAppList = Files.readLines(new File(appDataFileConstant), StandardCharsets.UTF_8);
            if (registeredAppList.size() < 1) {
                Collection<Application> registeredApplications = applicationRegistry.getApplications();
                for (Application registeredApplication : registeredApplications) {
                    java.nio.file.Files.write(Paths.get(appDataFileConstant), (registeredApplication.getName() + "|" + ((registeredApplication.getManagementUrl()).split("\\/|:"))[3] + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                }

                registeredAppList = Files.readLines(new File(appDataFileConstant), StandardCharsets.UTF_8);
            }


            for (String registeredApp : registeredAppList) {
                String[] data = registeredApp.split("\\|");
                if (data[1].toString().equalsIgnoreCase(InetAddress.getLocalHost().getHostName()) ||
                        data[1].toString().equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress()) ||
                        data[1].toString().equalsIgnoreCase(InetAddress.getLocalHost().getCanonicalHostName())) {
                    if (!isHostScannedForFiles.contains(data[1].toString())) {
                        isHostScannedForFiles.add(data[1].toString());
                        File folder = new File(folderLocation);
                        listOfFiles = folder.listFiles();
                    }
                } else {
                    if (!isHostScannedForFiles.contains(data[1].toString())) {
                        if (fileExtn.equalsIgnoreCase("jar")) {
                            listOfFilesArray = Arrays.asList(SSHConnectionManager.getAppFileNameListWithoutExtn(data[1].trim(), folderLocation, hostname, hostPass));
                        } else {
                            listOfFilesArray = Arrays.asList(SSHConnectionManager.getPidFileNameListWithoutExtn(data[1].trim(), folderLocation, hostname, hostPass));
                        }

                        //File file = new File(folderLocation + "c:\\data\\input-file.txt");
                        for (String listOfFile : listOfFilesArray) {
                            if (!listOfFile.isEmpty()) {
                                fileArrayList.add(listOfFile.substring(0, listOfFile.lastIndexOf('.')));
                            }
                        }
                    }

                }
            }
            if (listOfFiles != null) {
                LOGGER.debug("The Path::" + folderLocation + "has" + listOfFiles.length + "Files");
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        fileName = listOfFiles[i].getName();
                        System.out.println("File " + fileName);
                        fileArrayList.add(Files.getNameWithoutExtension(fileName));
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
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

    public void manageApplication(String appName, String hostName, ApplicationManagement appManagement, String manageFlag, String pId) {
        System.out.print(System.getProperty("os.name"));
        String command = null;
        System.out.println(appManagement.javaLocation);

        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        switch (ostype) {
            case Windows:
                break;
            case MacOS:
                break;
            case Linux:
                if (manageFlag.equalsIgnoreCase("start")) {
                    command = buildCommandToStartApplication(appManagement, appName);
                } else if (manageFlag.equalsIgnoreCase("stop")) {
                    command = buildCommandToStopApplication(pId);
                }
                LOGGER.debug("Executed command is" + command);
                //String output = util.executeCommand(command);
                //LOGGER.debug("Linux Command Executed" + output);
                try {
                    if (hostName.equalsIgnoreCase(InetAddress.getLocalHost().getHostName()) ||
                            hostName.equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress()) ||
                            hostName.equalsIgnoreCase(InetAddress.getLocalHost().getCanonicalHostName())) {
                        executeCommand(command);
                    } else {
                        SSHConnectionManager.runCommandOverSSH(hostName, appManagement.hostUsername, appManagement.hostPassword, command);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                break;
            case Other:
                break;
        }
    }

    public String buildCommandToStartApplication(ApplicationManagement appManagement, String appName) {
        return appManagement.javaLocation + "/bin/java -jar " + appManagement.appLocation + "/" + appName +
                ".jar > /dev/null 2>&1 &";
    }

    public String buildCommandToStopApplication(String pId) {
        return "kill -9 " + pId;
    }
}
