package de.codecentric.boot.admin.registry;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

@Component
public class ApplicationManagement{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationManagement.class);

   private final String javaLocation;

    private final String appLocation;

    private final String appConfigLocation;

    @Autowired
    public ApplicationManagement(String javaLocation, String appLocation, String appConfigLocation) {
        this.javaLocation = javaLocation;
        this.appLocation = appLocation;
        this.appConfigLocation = appConfigLocation;
        LOGGER.debug("================== " + javaLocation + "================== \n");
        LOGGER.debug("================== " + appLocation + "================== \n");
        LOGGER.debug("================== " + appConfigLocation + "================== \n");
    }

    public void manageApplication(String manageFlag) {

        String command=null;

        if(manageFlag.equalsIgnoreCase("start")){
            command= javaLocation+"/bin/java -jar "+appLocation+"--spring.config.location="+appConfigLocation+" > /dev/null 2>&1 &";

        }else if(manageFlag.equalsIgnoreCase("stop")){
            command= "ping -c 3 google.com";

        }
        LOGGER.debug("Executed command is"+command);
        String output = executeCommand(command);
        LOGGER.debug("Linux Command Executed"+output);

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
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public ArrayList getApplication() {

        ArrayList response = new ArrayList();
        File folder = new File(appLocation);
        File[] listOfFiles = folder.listFiles();

        LOGGER.debug("The Path::"+appLocation+"has"+listOfFiles.length+"Files/Folders");
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                response.add(listOfFiles[i].getName());

            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
                response.add(listOfFiles[i].getName());
            }
        }
        return response;
    }
}
