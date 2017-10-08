package de.codecentric.boot.admin.management;


import com.google.common.io.Files;
import de.codecentric.boot.admin.config.OsCheck;
import de.codecentric.boot.admin.registry.ApplicationRegistry;
import de.codecentric.boot.admin.management.bean.AppManagementBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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

    private String hostName = "";

    private final String hostUsername;

    private final String hostPassword;

    @Autowired
    ApplicationRegistry applicationRegistry;

    @Autowired
    AppManagementUtil util;

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
                //String output = util.executeCommand(command);
                //LOGGER.debug("Linux Command Executed" + output);

                break;
            case Other:
                break;
        }
    }





    public List<AppManagementBean> getAllApplication() {
        List<AppManagementBean> appManagementBeanList = new ArrayList<AppManagementBean>();

        ArrayList<String> AppFileArrayList = new ArrayList();
        ArrayList<String> PidFileArrayList = new ArrayList();
        String pidContent;
        AppFileArrayList = util.getFileNameListWithoutExtn(appLocation);
        PidFileArrayList = util.getFileNameListWithoutExtn(pidLocation);

        for (String appfile : AppFileArrayList) {

            AppManagementBean appManagementBean = new AppManagementBean();

            for (String pidfile : PidFileArrayList) {
                    try {
                        appManagementBean.setName(appfile);
                        if(applicationRegistry.getApplicationsByName(appfile).iterator().hasNext()) {
                            hostName = applicationRegistry.getApplicationsByName(appfile).iterator().next().getManagementUrl();
                            appManagementBean.setHostUrl(hostName);
                            //save the host details in DB/Memory
                            //byte[] strToBytes = (appfile+"|"+hostName).get();
                            Files.write((appfile + "|" + hostName).getBytes(), new File("registered-apps.txt"));
                        }else{
                            //appManagementBean.setHostUrl("NA");
                            //get the host details from DB/Memory
                            List<String> registeredAppList = Files.readLines(new File("registered-apps.txt"), StandardCharsets.UTF_8);

                            for (String registeredApp:registeredAppList) {
                                String[] data = registeredApp.split("\\|");
                                if(appfile.equalsIgnoreCase(data[0])){
                                    appManagementBean.setHostUrl(data[1]);
                                }
                            }
                        }

                        if (appfile.equalsIgnoreCase(pidfile)) {
                            pidContent = Files.readFirstLine(new File(pidLocation + "/" + pidfile + ".pid"), StandardCharsets.UTF_8);
                            appManagementBean.setPid(pidContent);
                            appManagementBean.setStatus(util.getStatusOfPid(pidContent));
                            break;
                        }else{
                            appManagementBean.setPid("NA");
                            appManagementBean.setStatus("FAILURE");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            appManagementBean.setHostUsername(hostUsername);
            appManagementBean.setHostPassword(hostPassword);
            appManagementBeanList.add(appManagementBean);
        }
        return appManagementBeanList;

    }


}