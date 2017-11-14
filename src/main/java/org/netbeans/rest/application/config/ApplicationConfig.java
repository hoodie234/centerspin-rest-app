package org.netbeans.rest.application.config;
import java.util.*;
import javax.ws.rs.core.Application;


@javax.ws.rs.ApplicationPath("v1")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.centerspin.rest.app.resources.ContentResource.class);
        resources.add(com.centerspin.rest.app.resources.PendingApprovalResource.class);
        resources.add(com.centerspin.rest.app.resources.ScraperResource.class);
        resources.add(com.centerspin.rest.app.resources.SubmitResource.class);
    }

}
