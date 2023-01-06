package vttp2022.paf.assessment.eshop.controllers;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;

@RestController
@RequestMapping(path="/api/order")
public class OrderController {

	@Autowired
	private CustomerRepository cusRepo;

	@Autowired
	private OrderRepository orderRepo;

	//TODO: Task 3
	@PostMapping(consumes = "application/json")
	public ResponseEntity<String> getGenres(@RequestBody String q) {

		System.out.println(q);
		JsonReader reader = Json.createReader(new StringReader(q));
        JsonObject results = reader.readObject();
		String name = results.getString("name");

		System.out.println(name);
		Optional<Customer> opt = cusRepo.findCustomerByName(name);

		if (opt.isEmpty()) {
			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("error", "Customer %s not found".formatted(name));

			return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.contentType(MediaType.APPLICATION_JSON)
            	.body(job.build().toString());
		}

		Customer c = opt.get();
		OrderStatus os = new OrderStatus();

		try {
			os = orderRepo.saveOrder(results, c);

		} catch (Exception ex) {
			System.out.println(">>>>> " + ex.getMessage());

			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("error", ex.getMessage());

			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.contentType(MediaType.APPLICATION_JSON)
            	.body(job.build().toString());
		}

		// JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
		// for (String g: genres)
		// 	arrBuilder.add(g);

		JsonObjectBuilder job = Json.createObjectBuilder();

		if (os.getDeliveryId().equals("")) {
			job.add("orderId", os.getOrderId())
				.add("status", os.getStatus());
		} else {
			job.add("orderId", os.getOrderId())
				.add("deliveryId", os.getDeliveryId())
				.add("status", os.getStatus());
		}

		//return ResponseEntity.ok(arrBuilder.build().toString());
		return ResponseEntity
			.status(HttpStatus.OK)
			.contentType(MediaType.APPLICATION_JSON)
			.body(job.build().toString());
	}

	@GetMapping(path="/{name}/status", produces = "application/json")
	public ResponseEntity<String> getTotalDispatchedPending(@PathVariable String name) {

		System.out.println(name);

		JsonObject jo = orderRepo.getTotalDispatchedPendingInRepo(name);

		return ResponseEntity
			.status(HttpStatus.OK)
			.contentType(MediaType.APPLICATION_JSON)
			.body(jo.toString());
	}

}
