package com.targa.labs.quarkushop.web;


import com.targa.labs.quarkushop.security.TokenService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/user")
@Tag(name = " user", description = "All the user methods")
public class UserResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    TokenService tokenService;

    @POST
    @Path("/access-token")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAccessToken(@QueryParam("username") String username, @QueryParam("password") String password) throws IOException, InterruptedException {
        return tokenService.getAccessToken(username, password);
    }

    @GET
    @RolesAllowed("user")
    @Path("/current/info")
    public JsonWebToken getCurrentUserInfo() {
        return jwt;
    }

    @GET()
    @Path("/current/info-alternative")
    public Principal getCurrentUserInfoAlternative(@Context SecurityContext ctx) {
        return ctx.getUserPrincipal();
    }

    @GET
    @RolesAllowed("user")
    @Path("/current/info/claims")
    public Map<String, Object> getCurrentUserInfoClaims() {
        return jwt.getClaimNames()
                .stream()
                .map(name -> Map.entry(name, jwt.getClaim(name)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue)
                );
    }
}
