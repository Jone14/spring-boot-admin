package de.codecentric.boot.admin.management;

import com.google.common.io.Files;
import de.codecentric.boot.admin.config.OsCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

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
        ArrayList fileArrayList = new ArrayList();
        String fileName;
        File folder = new File(folderLocation);
        File[] listOfFiles = folder.listFiles();
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
            /*BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    /*public void test(){
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        Hashtable<String,String> config = new Hashtable<String,String>();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        try {
            session.connect(60000);
            ChannelShell channel = (ChannelShell) session.openChannel("shell");
            Expect4j expect = new Expect4j(channel.getInputStream(), channel.getOutputStream());
            channel.connect();
        }catch(Exception E){

        }

    }*/
}
