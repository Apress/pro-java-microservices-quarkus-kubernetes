package com.targa.labs.quarkushop.order.web;

import com.targa.labs.quarkushop.order.dto.OrderDto;
import com.targa.labs.quarkushop.order.service.OrderService;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Nebrass Lamouchi
 */

@Authenticated
@Path("/orders")
@Tag(name = "order", description = "All the order methods")
public class OrderResource {

    @Inject
    OrderService orderService;

    @RolesAllowed("admin")
    @GET
    public List<OrderDto> findAll() {
        return this.orderService.findAll();
    }

    @GET
    @Path("/customer/{id}")
    public List<OrderDto> findAllByUser(@PathParam("id") Long id) {
        return this.orderService.findAllByUser(id);
    }

    @GET
    @Path("/{id}")
    public OrderDto findById(@PathParam("id") Long id) {
        return this.orderService.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public OrderDto create(OrderDto orderDto) {
        return this.orderService.create(orderDto);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        this.orderService.delete(id);
    }

    @GET
    @Path("/exists/{id}")
    public boolean existsById(@PathParam("id") Long id) {
        return this.orderService.existsById(id);
    }

    @GET
    @Path("/payment/{id}")
    public OrderDto findByPaymentId(@PathParam("id") Long id) {
        return this.orderService.findByPaymentId(id);
    }
}
