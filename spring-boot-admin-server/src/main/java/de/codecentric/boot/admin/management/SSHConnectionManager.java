package de.codecentric.boot.admin.management;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

public class SSHConnectionManager {

    private Session session;

    private String username = "pbarega";
    private String password = "Ready2go9";
    private String hostname = "ldgrvtibadmr002.ladsys.net";

    public SSHConnectionManager() {
    }

    public SSHConnectionManager(String hostname, String username, String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    public void open() throws JSchException {
        open(this.hostname, this.username, this.password);
    }

    public void open(String hostname, String username, String password) throws JSchException {

        JSch jSch = new JSch();

        this.session = jSch.getSession(username, hostname, 22);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");  // not recommended
        this.session.setConfig(config);
        this.session.setPassword(password);

        System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
        this.session.connect();
        System.out.println("Connected!");
    }

    public String runCommand(String command) throws JSchException, IOException {

        String ret = "";

        if (!this.session.isConnected()) {
            throw new RuntimeException("Not connected to an open session.  Call open() first!");
        }

        ChannelExec channel = null;
        channel = (ChannelExec) this.session.openChannel("exec");

        channel.setCommand(command);
        channel.setInputStream(null);

        PrintStream out = new PrintStream(channel.getOutputStream());
        InputStream in = channel.getInputStream(); // channel.getInputStream();

        channel.connect();

        // you can also send input to your running process like so:
        // String someInputToProcess = "something";
        // out.println(someInputToProcess);
        // out.flush();

        ret = getChannelOutput(channel, in);

        channel.disconnect();

        System.out.println("Finished sending commands!");

        return ret;
    }


    private String getChannelOutput(Channel channel, InputStream in) throws IOException {

        byte[] buffer = new byte[1024];
        StringBuilder strBuilder = new StringBuilder();

        String line = "";
        while (true) {
            while (in.available() > 0) {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) {
                    break;
                }
                strBuilder.append(new String(buffer, 0, i));
                System.out.println(line);
            }

            if (line.contains("logout")) {
                break;
            }

            if (channel.isClosed()) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }

        return strBuilder.toString();
    }

    public void close() {
        this.session.disconnect();
        System.out.println("Disconnected channel and session");
    }


    public static void main(String[] args) {

        SSHConnectionManager ssh = new SSHConnectionManager();
        try {
            ssh.open();
            String ret = ssh.runCommand("ls /home/pbarega/spring-boot-admin/data-fabric-jars");

            System.out.println(ret);
            String[] ary = ret.split("\n");
            System.out.println(ary);
            ssh.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String[] getAppsOverSSH(String host, String url) {

        return null;
    }
}
