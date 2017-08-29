
package com.mycompany.tlog16rs.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


public class Greeting {
    
    @JsonProperty
    private String greeting;
    
    public Greeting(){
    }
    
    public Greeting(String greeting){
        this.greeting = greeting;
    }
    
    public String getGreeting(){
        return greeting;
    }
    
 
    
    
}
