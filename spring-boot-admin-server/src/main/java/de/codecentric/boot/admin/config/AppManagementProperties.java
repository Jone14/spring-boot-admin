package de.codecentric.boot.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix ="application.management")
public class AppManagementProperties {
    private String javaLocation = "";
    private String jarLocation = "";
    private String configLocation = "";

    public void setJavaLocation(String javaLocation){
        this.jarLocation = javaLocation;
    }

    public String getJavaLocation() {
        return javaLocation;
    }

    public String getJarLocation() {
        return jarLocation;
    }

    public void setJarLocation(String jarLocation) {
        this.jarLocation = jarLocation;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }
}
