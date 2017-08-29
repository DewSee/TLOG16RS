package com.mycompany.tlog16rs.resources;

import com.mycompany.tlog16rs.core.Greeting;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Path("/hello")
public class TLOG16RSResource {

   
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello world!";
    }
    
    @Path("/{name}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNameGreeting(@PathParam(value = "name") String name){
        return "Hello " + name;
    }
    
    @Path("/query_param")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNamedStringWithParam(@DefaultValue("world") @QueryParam("name") String name){
        return "Hello " +name;
    }
    
    @Path("/hello_json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting getJSONGreeting(){
        return new Greeting("Hello world!");
    }
    
}
