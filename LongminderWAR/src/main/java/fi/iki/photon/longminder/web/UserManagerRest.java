package fi.iki.photon.longminder.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.iki.photon.longminder.entity.dto.UserDTO;

/**
 * Rest services for UserManagerService
 * 
 * TODO everything.
 */

@Path("/auth")
@Produces(MediaType.TEXT_PLAIN)
@Stateless
public class UserManagerRest {

    @EJB
    private UserManagerService ums;

    /**
     * Default constructor.
     */
    public UserManagerRest() {
    }

    @GET
    @Path("ping")
    public String ping() {
        return "Hello";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("login")
    public Response login(@FormParam(value = "name") String name,
            @FormParam(value = "password") String password,
            @Context HttpServletRequest req) {
        JsonResponse response = new JsonResponse();
        System.out.println("Login " + name + " " + password);
        if (!ums.login(name, password, req)) {
            response.setStatus("FAILED");
            response.setErrorMsg("Failed login");
            return Response.ok().entity(response).build();
        }

        response.setStatus("SUCCESS");

        return Response.ok().entity(response).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("register")
    public Response register(UserDTO ud, @Context HttpServletRequest req) {
        JsonResponse response = new JsonResponse();

        if (!ums.register(ud, req)) {
            response.setStatus("FAILED");
            response.setErrorMsg("Invalid username or password");
            return Response.ok().entity(response).build();
        }

        response.setStatus("SUCCESS");

        return Response.ok().entity(response).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("logout")
    public Response logout(@Context HttpServletRequest req) {
        JsonResponse response = new JsonResponse();

        ums.logout(req);

        response.setStatus("SUCCESS");

        return Response.ok().entity(response).build();
    }

}
