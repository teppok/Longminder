package fi.iki.photon.longminder.web;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.iki.photon.longminder.LongminderException;
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
    public Response login(@FormParam(value = "name") final String name,
            @FormParam(value = "password") final String password,
            @Context final HttpServletRequest req) {
        final JsonResponse response = new JsonResponse();
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
    public Response register(final UserDTO ud,
            @Context final HttpServletRequest req) {
        final JsonResponse response = new JsonResponse();

        try {
            ums.register(ud, req);
        } catch (LongminderException e) {
            response.setStatus("FAILED");
            response.setErrorMsg("Invalid username or password");
            return Response.ok().entity(response).build();
        }

        response.setStatus("SUCCESS");

        return Response.ok().entity(response).build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("logout")
    public Response logout(@Context final HttpServletRequest req) {
        final JsonResponse response = new JsonResponse();

        ums.logout(req);

        response.setStatus("SUCCESS");

        return Response.ok().entity(response).build();
    }

}
