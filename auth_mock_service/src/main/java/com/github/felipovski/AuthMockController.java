package com.github.felipovski;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthMockController {

    @POST
    @Path("/userInfo")
    public Response userInfo(InfoRequestDto dto) {

        return Response
                .ok(new InfoResponseDto("felipovski", "Felipe", "Felipe", "felipe@felipe.felipe"))
                .build();
    }

    @POST
    @Path("/auth")
    public boolean userInfo(AuthUserDto dto) {
        return "username".equals(dto.username()) && "password".equals(dto.password());

    }
}
