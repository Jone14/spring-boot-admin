package de.codecentric.boot.admin.registry.web;

import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.registry.ApplicationManagement;
import de.codecentric.boot.admin.registry.ApplicationRegistry;
import de.codecentric.boot.admin.web.AdminController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * REST controller for controlling registration of managed applications.
 */
@AdminController
@ResponseBody
@RequestMapping("/api/applications")
public class RegistryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryController.class);

    private final ApplicationRegistry registry;

    private final ApplicationManagement appManagement;

    public RegistryController(ApplicationRegistry registry, ApplicationManagement appManagement) {
        this.registry = registry;
        this.appManagement = appManagement;
    }

    /**
     * Register an application within this admin application.
     *
     * @param application The application infos.
     * @return The registered application.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Application> register(@RequestBody Application application) {
        Application applicationWithSource = Application.copyOf(application).withSource("http-api")
                .build();
        LOGGER.debug("Register application {}", applicationWithSource.toString());
        Application registeredApp = registry.register(applicationWithSource);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredApp);
    }

    /**
     * List all registered applications with name
     *
     * @param name the name to search for
     * @return List
     */
    @RequestMapping(method = RequestMethod.GET)
    public Collection<Application> applications(
            @RequestParam(value = "name", required = false) String name) {
        LOGGER.debug("Deliver registered applications with name={}", name);
        if (name == null || name.isEmpty()) {
            return registry.getApplications();
        } else {
            return registry.getApplicationsByName(name);
        }
    }

    /**
     * Get a single application out of the registry.
     *
     * @param id The application identifier.
     * @return The registered application.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable String id) {
        LOGGER.debug("Deliver registered application with ID '{}'", id);
        Application application = registry.getApplication(id);
        if (application != null) {
            return ResponseEntity.ok(application);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Unregister an application within this admin application.
     *
     * @param id The application id.
     * @return the unregistered application.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> unregister(@PathVariable String id) {
        LOGGER.debug("Unregister application with ID '{}'", id);
        Application application = registry.deregister(id);
        if (application != null) {
            return ResponseEntity.ok(application);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "startApplication", method = RequestMethod.GET)
    public String startApplication() throws Exception {
        try {
            LOGGER.debug("Starting Spring Boot Application");
            appManagement.manageApplication("start");
        } catch (Exception exception) {
            LOGGER.error("Exception occured while starting Spring Boot Application", exception.getStackTrace());
        }
        return "Application Started";
    }

    @RequestMapping(value = "stopApplication", method = RequestMethod.GET)
    public String stopApplication() throws Exception {
        try {
            LOGGER.debug("Stopping Spring Boot Application");
            appManagement.manageApplication("stop");
        } catch (Exception exception) {
            LOGGER.error("Exception occured while stopping Spring Boot Application", exception.getStackTrace());
        }
        return "Application Stopped";
    }

    @RequestMapping(value = "getApplication", method = RequestMethod.GET)
    public ArrayList getApplication() throws Exception {
        ArrayList response = new ArrayList();
        try {
            LOGGER.debug("Getting list of Spring Boot Application");
            response = appManagement.getApplication();
        } catch (Exception exception) {
            LOGGER.error("Exception occured while Getting list of Spring Boot Application", exception.getStackTrace());
        }
        return response;
    }




}
