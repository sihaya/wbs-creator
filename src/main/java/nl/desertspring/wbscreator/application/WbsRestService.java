/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.application;

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
import nl.desertspring.wbscreator.domain.Project;
import nl.desertspring.wbscreator.domain.Sheet;

/**
 *
 * @author sihaya
 */
@Stateless
@LocalBean
@Path("wbs")
public class WbsRestService {

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
    @Produces("application/xml")
    public List<Project> listProjects(@PathParam("username")String username) {
        return wbsService.findProjectsByUsername(username);
    } 
    
    @POST
    @Path("project")
    public Response createProject(@FormParam("username")String username, @FormParam("projectName")String projectName) {
        Project project = wbsService.createProject(username, projectName);
        
        return Response.created(context.getAbsolutePathBuilder().path(project.getProjectId()).build()).build();
    }
    
    @GET
    @Path("project/{sheetId}")
    @Produces("application/xml")
    public List<Sheet> listSheets(@PathParam("sheetId")String sheetId) {
        return wbsService.findSheetsByProjectId(sheetId);
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
