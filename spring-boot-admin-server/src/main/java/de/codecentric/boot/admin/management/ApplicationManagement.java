package de.codecentric.boot.admin.management;


import com.google.common.io.Files;
import de.codecentric.boot.admin.management.bean.AppManagementBean;
import de.codecentric.boot.admin.registry.ApplicationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationManagement {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationManagement.class);
    public final String javaLocation;
    public final String appLocation;
    public final String appConfigLocation;
    public final String pidLocation;
    public final String hostUsername;
    public final String hostPassword;
    @Autowired
    ApplicationRegistry applicationRegistry;
    @Autowired
    AppManagementUtil util;
    private String hostName = "";

    @Autowired
    public ApplicationManagement(String javaLocation, String appLocation, String appConfigLocation, String pidLocation, String hostUsername, String hostPassword) {
        this.javaLocation = javaLocation;
        this.appLocation = appLocation;
        this.appConfigLocation = appConfigLocation;
        this.pidLocation = pidLocation;
        this.hostUsername = hostUsername;
        this.hostPassword = hostPassword;
    }

    public String startApplication(String appName, String hostname) {
        util.manageApplication(appName, hostname, this, "start", "");
        return null;
    }

    public String stopApplication(String pId, String hostname) {
        util.manageApplication("", hostname, this, "stop", pId);
        return null;
    }

    public List<AppManagementBean> getAllApplication() throws IOException {
        List<AppManagementBean> appManagementBeanList = new ArrayList<AppManagementBean>();

        ArrayList<String> AppFileArrayList = new ArrayList();
        ArrayList<String> PidFileArrayList = new ArrayList();
        String pidContent;
        AppFileArrayList = util.getFileNameListWithoutExtn(appLocation, hostUsername, hostPassword, "jar");
        PidFileArrayList = util.getFileNameListWithoutExtn(pidLocation, hostUsername, hostPassword, "pid");

        for (String appfile : AppFileArrayList) {
            AppManagementBean appManagementBean = new AppManagementBean();
            for (String pidfile : PidFileArrayList) {
                appManagementBean.setName(appfile);
                if (applicationRegistry.getApplicationsByName(appfile).iterator().hasNext()) {
                    hostName = applicationRegistry.getApplicationsByName(appfile).iterator().next().getManagementUrl();
                    //save the host details in DB/Memory
                    //byte[] strToBytes = (appfile+"|"+hostName).get();
                    String host = ((hostName).split("\\/|:"))[3];
                    appManagementBean.setHostUrl(host);
                    java.nio.file.Files.write(Paths.get("registered-apps.txt"), (appfile + "|" + host + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                } else {
                    //appManagementBean.setHostUrl("NA");
                    //get the host details from DB/Memory
                    List<String> registeredAppList = null;
                    registeredAppList = java.nio.file.Files.readAllLines(Paths.get("registered-apps.txt"), StandardCharsets.UTF_8);
                    ArrayList hostUrlList = new ArrayList();
                    for (String registeredApp : registeredAppList) {
                        String[] data = registeredApp.split("\\|");
                        if (appfile.equalsIgnoreCase(data[0])) {
                            appManagementBean.setHostUrl(data[1]);
                            if (!hostUrlList.contains(data[1])) {
                                hostUrlList.add(data[1]);
                            }
                        }
                    }
                }

                if (PidFileArrayList.contains(appfile)) {
                    File pidFile = new File(pidLocation + "/" + appfile + ".pid");
                    pidContent = Files.readFirstLine(pidFile, StandardCharsets.UTF_8);
                    appManagementBean.setPid(pidContent);
                    appManagementBean.setStatus(util.getStatusOfPid(pidContent));
                    break;
                } else {
                    appManagementBean.setPid("NA");
                    appManagementBean.setStatus("FAILURE");
                }
            }
            appManagementBean.setHostUsername(hostUsername);
            appManagementBean.setHostPassword(hostPassword);
            appManagementBeanList.add(appManagementBean);
        }
        return appManagementBeanList;
    }

}
