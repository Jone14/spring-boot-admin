package de.codecentric.boot.admin.registry;


import com.google.common.io.Files;
import de.codecentric.boot.admin.config.OsCheck;
import de.codecentric.boot.admin.registry.bean.AppManagementBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationManagement {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationManagement.class);

    private final String javaLocation;

    private final String appLocation;

    private final String appConfigLocation;

    private final String pidLocation;

    private final String hostName = "";

    private final String hostUsername;

    private final String hostPassword;

    @Autowired
    public ApplicationManagement(String javaLocation, String appLocation, String appConfigLocation, String pidLocation, String hostUsername, String hostPassword) {
        this.javaLocation = javaLocation;
        this.appLocation = appLocation;
        this.appConfigLocation = appConfigLocation;
        this.pidLocation = pidLocation;
        this.hostUsername = hostUsername;
        this.hostPassword = hostPassword;
    }

    public String buildCommandToStartApplication() {
        return javaLocation + "/bin/java -jar " + appLocation + "--spring.config.location=" + appConfigLocation + " > /dev/null 2>&1 &";
    }

    public String buildCommandToStopApplication() {
        return null;
    }

    public void manageApplication(String manageFlag) {
        System.out.print(System.getProperty("os.name"));
        String command = null;

        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        switch (ostype) {
            case Windows:
                break;
            case MacOS:
                break;
            case Linux:
                if (manageFlag.equalsIgnoreCase("start")) {
                    command = buildCommandToStartApplication();
                } else if (manageFlag.equalsIgnoreCase("stop")) {
                    command = buildCommandToStopApplication();
                }
                LOGGER.debug("Executed command is" + command);
                String output = executeCommand(command);
                LOGGER.debug("Linux Command Executed" + output);

                break;
            case Other:
                break;
        }
    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public ArrayList getFileNameListWithoutExtn(String folderLocation) {
        ArrayList fileArrayList = new ArrayList();
        String fileName;
        File folder = new File(folderLocation);
        File[] listOfFiles = folder.listFiles();
        LOGGER.debug("The Path::" + appLocation + "has" + listOfFiles.length + "Files");
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileName = listOfFiles[i].getName();
                System.out.println("File " + fileName);
                fileArrayList.add(Files.getNameWithoutExtension(fileName));
            }
        }
        return fileArrayList;
    }

    public List<AppManagementBean> getAllApplication() {
        List<AppManagementBean> appManagementBeanList = new ArrayList<AppManagementBean>();
        AppManagementBean appManagementBean = new AppManagementBean();
        ArrayList<String> AppFileArrayList = new ArrayList();
        ArrayList<String> PidFileArrayList = new ArrayList();
        String pidContent;
        AppFileArrayList = getFileNameListWithoutExtn(appLocation);
        PidFileArrayList = getFileNameListWithoutExtn(pidLocation);

        for (String appfile : AppFileArrayList) {
            for (String pidfile : PidFileArrayList) {
                if (appfile.equalsIgnoreCase(pidfile))
                    try {
                        pidContent = Files.readFirstLine(new File(pidLocation + "/" + pidfile + ".pid"), StandardCharsets.UTF_8);
                        appManagementBean.setName(appfile);
                        appManagementBean.setPid(pidContent);
                        appManagementBean.setStatus(getStatusOfPid(pidContent));
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            appManagementBeanList.add(appManagementBean);
        }
        return appManagementBeanList;

    }

    public String getStatusOfPid(String pid) {
        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        String output = "";
        switch (ostype) {
            case Windows:
                break;
            case MacOS:
                break;
            case Linux:
                String command = "ps -ef | grep " + pid;
                LOGGER.debug("Executed command is" + command);
                output = executeCommand(command);
                LOGGER.debug("Linux Command Executed" + output);

                break;
            case Other:
                break;
        }
        return output;
    }
}