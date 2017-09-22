package de.codecentric.boot.admin.management;

import de.codecentric.boot.admin.registry.ApplicationManagement;
import de.codecentric.boot.admin.web.AdminController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@AdminController
@ResponseBody
@RequestMapping("/api/management")
public class ManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementController.class);

    private final ApplicationManagement appManagement;

    public ManagementController(ApplicationManagement appManagement) {
        this.appManagement = appManagement;
    }

    @RequestMapping(value="startApplication",method= RequestMethod.GET)
    public String startApplication()throws Exception{
        try{
            LOGGER.debug("Starting Spring Boot Application");
            appManagement.manageApplication("start");
        }catch(Exception exception){
            LOGGER.error("Exception occured while starting Spring Boot Application",exception.getStackTrace());
        }
        return "Application Started";
    }

    @RequestMapping(value="stopApplication",method= RequestMethod.GET)
    public String stopApplication()throws Exception{
        try{
            LOGGER.debug("Stopping Spring Boot Application");
            appManagement.manageApplication("stop");
        }catch(Exception exception){
            LOGGER.error("Exception occured while stopping Spring Boot Application",exception.getStackTrace());
        }
        return "Application Stopped";
    }

    @RequestMapping(value="getApplication",method= RequestMethod.GET)
    public ArrayList getApplication()throws Exception{
        ArrayList response = new ArrayList();
        try{
            LOGGER.debug("Getting list of Spring Boot Application");
            response = appManagement.getApplication();
        }catch(Exception exception){
            LOGGER.error("Exception occured while Getting list of Spring Boot Application",exception.getStackTrace());
        }
        return response;
    }


}
