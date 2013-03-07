package fi.iki.photon.longminder.web;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * A simple exception mapper provider.
 * 
 */
@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Response RESPONSE;
    private static final JsonResponse JSON = new JsonResponse("ERROR");

    static {
        RESPONSE = Response.status(500).entity(ThrowableExceptionMapper.JSON)
                .build();
    }

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(final Throwable ex) {
        System.out.println("ThrowableExceptionMapper: " + ex.getClass());
        ex.printStackTrace();
        // usually you don't pass detailed info out (don't do this here in
        // production environments)
        ThrowableExceptionMapper.JSON.setErrorMsg(ex.getMessage());

        return ThrowableExceptionMapper.RESPONSE;
    }

}