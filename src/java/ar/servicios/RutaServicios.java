/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.servicios;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
/**
 *
 * @author rianveal
 */

@Stateless
@Path("servicios")
public class RutaServicios extends Servicios {
  
  @GET
  @Path("/user/validateCredentials/{user}/{password}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response validateCredentials(@PathParam("user") String user, @PathParam("password") String password) {
    Response.ResponseBuilder response = Response.ok(validate_credentials(user, password));
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/products")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response allProducts() {
    Response.ResponseBuilder response = Response.ok(get_all_products());
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/product/add/{data}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addProduct(@PathParam("data") String data) {
    Response.ResponseBuilder response = Response.ok(add_product(data));
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/product/find/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response findProductID(@PathParam("id") String id) {
    Response.ResponseBuilder response = Response.ok(find_product(id));
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/product/addMovement/{data}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addMovementProduct(@PathParam("data") String data) {
    Response.ResponseBuilder response = Response.ok(add_movement_product(data));
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/product/update/{data}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateProduct(@PathParam("data") String data) {
    Response.ResponseBuilder response = Response.ok(update_product(data));
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/brands")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response allBrands() {
    Response.ResponseBuilder response = Response.ok(get_all_brands());
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/brand/add/{data}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addBrand(@PathParam("data") String data) {
    Response.ResponseBuilder response = Response.ok(add_brand(data));
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/categories")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response allCategories() {
    Response.ResponseBuilder response = Response.ok(get_all_categories());
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
  
  @GET
  @Path("/category/add/{data}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addCategry(@PathParam("data") String data) {
    Response.ResponseBuilder response = Response.ok(add_category(data));
    response.header("Access-Control-Allow-Origin", "*");
    return response.build();
  }
}
