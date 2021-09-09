package com.targa.labs.quarkushop.customer.client;

import com.targa.labs.quarkushop.customer.dto.OrderDto;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Optional;

@Path("/api/orders")
@RegisterRestClient
public interface OrderRestClient {

    @GET
    @Path("/{id}")
    @CircuitBreaker(requestVolumeThreshold = 10, delay = 15000)
    @Retry(maxRetries = 4)
    @Timeout(500)
    Optional<OrderDto> findById(@PathParam Long id);

    @GET
    @Path("/payment/{id}")
    @CircuitBreaker(requestVolumeThreshold = 10, delay = 15000)
    @Retry(maxRetries = 4)
    @Timeout(500)
    Optional<OrderDto> findByPaymentId(@PathParam Long id);

    @POST
    @CircuitBreaker(requestVolumeThreshold = 10, delay = 15000)
    @Retry(maxRetries = 4)
    @Timeout(500)
    OrderDto save(OrderDto order);
}
