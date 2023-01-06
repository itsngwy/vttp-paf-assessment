package vttp2022.paf.assessment.eshop.services;

import java.io.StringReader;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

@Service
public class WarehouseService {

	// You cannot change the method's signature
	// You may add one or more checked exceptions
	public OrderStatus dispatch(Order order) {

		// TODO: Task 4
		JsonArrayBuilder jab = Json.createArrayBuilder();

		for (LineItem li: order.getLineItems()) {
			jab.add(Json.createObjectBuilder().add("item", li.getItem()).add("quantity", li.getQuantity()));
		}

		JsonObject jo = Json.createObjectBuilder()
				.add("orderId", order.getOrderId())
				.add("name", order.getName())
				.add("address", order.getAddress())
				.add("email", order.getEmail())
				.add("lineItems", jab.build())
				.add("createdBy", "NG WEI YANG")
				.build();

		System.out.println("\nMy Json Request: " + jo);

		// Construct the request to the Http
		RequestEntity<String> req = RequestEntity
			.post("http://paf.chuklee.com/dispatch/%s".formatted(order.getOrderId()))
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.body(jo.toString());

		// Create the RestTemplate
		RestTemplate template = new RestTemplate();

		// Make the request to the our ticketing page "http://localhost:8080/purchase" 
		// and we will get back a response from it
		ResponseEntity<String> resp;
		String payload = "";
		OrderStatus os = new OrderStatus();

            try {
                // Throws an exception if status code not in between 200 - 399
                // Get response from the client with our request
                resp = template.exchange(req, String.class);
				// We get the body of the response
				payload = resp.getBody();
            } catch (Exception ex) {
                // This replaces the one below
                System.err.printf("Error: %s\n", ex.getMessage());
				os.setOrderId(order.getOrderId());
				os.setStatus("pending");
                return os;
            }
		
		// Print out the response
		System.out.printf(">>> Delivery response:\n%s\n", payload);
		
		JsonReader reader = Json.createReader(new StringReader(payload));
		JsonObject results = reader.readObject();
		String deliveryId = results.getString("deliveryId");

		os.setOrderId(order.getOrderId());
		os.setDeliveryId(deliveryId);
		os.setStatus("dispatched");

		return os;
	}
}
