/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import nl.desertspring.wbscreator.domain.Project;
import nl.desertspring.wbscreator.domain.Sheet;
import nl.desertspring.wbscreator.domain.Task;

/**
 *
 * @author sihaya
 */
@Stateless
@LocalBean
@Path("wbs")
public class WbsRestService {

    @Provider
    public static class JAXBContextResolver implements ContextResolver<JAXBContext> {

        private JAXBContext context;        

        public JAXBContextResolver() throws Exception {
            JSONConfiguration config = JSONConfiguration.natural().build();
            context = new JSONJAXBContext(config, "nl.desertspring.wbscreator");
        }

        @Override
        public JAXBContext getContext(Class<?> objectType) {
            return context;
        }
    }
    private UriInfo context;
    private WbsService wbsService;

    @POST
    @Path("user")
    public Response createUser(@FormParam("username") String username, @FormParam("password") String password, @FormParam("email") String email) {
        wbsService.createUser(username, password, email);

        return Response.created(context.getAbsolutePath()).build();
    }

    @GET
    @Path("user/{username}")
    @Produces("application/json")
    public List<Project> listProjects(@PathParam("username") String username) {
        return wbsService.findProjectsByUsername(username);
    }

    @POST
    @Path("project")
    public Response createProject(@FormParam("username") String username, @FormParam("projectName") String projectName) {
        Project project = wbsService.createProject(username, projectName);

        return Response.created(context.getAbsolutePathBuilder().path(project.getProjectId()).build()).build();
    }

    @GET
    @Path("project/{projectId}")
    @Produces("application/json")
    public List<Sheet> listSheets(@PathParam("projectId") String projectId) {
        return wbsService.findSheetsByProjectId(projectId);
    }

    @POST
    @Path("sheet")
    public Response createSheet(@FormParam("projectId") String projectId, @FormParam("sheetName") String sheetName) {
        Sheet sheet = wbsService.createSheet(projectId, sheetName);

        return Response.created(context.getAbsolutePathBuilder().path(sheet.getSheetId()).build()).build();
    }

    @GET
    @Path("sheet/{sheetId}")
    @Produces("application/json")
    public Sheet fetchSheetDetail(@PathParam("sheetId") String sheetId) {
        Sheet sheet = wbsService.fetchSheetDetail(sheetId);

        return sheet;
    }
    
    @GET
    @Path("sheet/public/{publicSecret}")
    @Produces("application/json")
    public Sheet fetchSheetDetailPublic(@PathParam("publicSecret")String publicSecret) {
        Sheet sheet = wbsService.findSheetByPublicSecret(publicSecret);
        
        return wbsService.fetchSheetDetail(sheet.getSheetId());
    }
    
    @POST
    @Path("sheet/public")
    public Response createSheet(@FormParam("sheetId") String sheetId) {
        wbsService.grantPublicRead(sheetId);
        
        Sheet sheet = wbsService.fetchSheetDetail(sheetId);

        return Response.created(context.getAbsolutePathBuilder().path(sheet.getPublicSecret()).build()).build();
    }

    @POST
    @Path("task")
    public Response createTask(@FormParam("parentTaskId") String parentTaskId) {
        Task task = wbsService.createTask(parentTaskId);

        return Response.created(context.getAbsolutePathBuilder().path(task.getTaskId()).build()).build();
    }

    @GET
    @Path("task/{taskId}")
    @Produces("application/json")
    public Task fetchTaskDetail(@PathParam("taskId") String taskId) {
        Task task = new Task();
        task.setTaskId(taskId);

        return task;
    }

    @PUT
    @Path("task")
    public Response updateTask(@FormParam("taskId") String taskId, @FormParam("effort") Integer effort, @FormParam("name") String name) {
        wbsService.updateTask(taskId, effort, name);

        return Response.created(context.getAbsolutePathBuilder().path(taskId).build()).build();
    }
    
    @POST
    @Path("project/{projectId}/members/{username}")
    public void updateTask(@PathParam("projectId")String projectId, @PathParam("username")String username) {
        wbsService.addMemberToProject(projectId, username);
    }
    
    @DELETE
    @Path("task")
    public void deleteTask(@FormParam("taskId")String taskId) {
        wbsService.deleteTask(taskId);
    }
    

    @Inject
    public void setWbsService(WbsService wbsService) {
        this.wbsService = wbsService;
    }

    @Context
    public void setContext(UriInfo context) {
        this.context = context;
    }
}
