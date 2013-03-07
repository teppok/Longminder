package fi.iki.photon.longminder.web;

import javax.inject.Singleton;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A simple Json provider
 * 
 */

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class MyJacksonJsonProvider implements ContextResolver<ObjectMapper> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // since Jackson 1.9
        // this default configuration can be overwritten via annotations
        MyJacksonJsonProvider.MAPPER
                .setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    public MyJacksonJsonProvider() {
        System.out
                .println("Constructor called: com.nabisoft.rs.provider.MyJacksonProvider");
    }

    @Override
    public ObjectMapper getContext(final Class<?> type) {
        System.out.println("MyJacksonProvider.getContext(): " + type);
        return MyJacksonJsonProvider.MAPPER;
    }
}