package vttp2022.paf.assessment.eshop.respositories;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.services.WarehouseService;

import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

@Repository
public class OrderRepository {
	// TODO: Task 3
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private WarehouseService warehouseService;

	public OrderStatus saveOrder(JsonObject results, Customer c) {
		String name = results.getString("name");
		JsonArray ja = results.getJsonArray("lineItems");

		String orderId = UUID.randomUUID().toString().substring(0, 8);
        System.out.printf(">>>> OrderId: %s\n", orderId);

		System.out.println(LocalDateTime.now());

		Integer countOrderInsert = jdbcTemplate.update(SQL_INSERT_ORDER, orderId, name, LocalDateTime.now().toString());
		System.out.println(countOrderInsert);
		Integer countLIInset = -1;
		
		List<LineItem> list = new LinkedList<>();

		for (int i = 0; i < ja.size(); i++) {
			JsonObject jo = ja.getJsonObject(i);
			LineItem li = new LineItem();
			li.setItem(jo.getString("item"));
			li.setQuantity(jo.getInt("quantity"));
			list.add(li);
			countLIInset = jdbcTemplate.update(SQL_INSERT_LINEITEM, li.getItem(), li.getQuantity(), orderId);
		}

		System.out.println("Order Insert: " + countOrderInsert);
		System.out.println("Line Item Insert: " + countLIInset);

		Order o = new Order();
		o.setOrderId(orderId);
		o.setName(name);
		o.setAddress(c.getAddress());
		o.setEmail(c.getEmail());
		o.setLineItems(list);

		OrderStatus os = warehouseService.dispatch(o);

		if (os.getDeliveryId() == null) {
			jdbcTemplate.update(SQL_INSERT_ORDER_STATUS, os.getOrderId(), "", os.getStatus(), LocalDateTime.now().toString());
		} else {
			jdbcTemplate.update(SQL_INSERT_ORDER_STATUS, os.getOrderId(), os.getDeliveryId(), os.getStatus(), LocalDateTime.now().toString());
		}
		
		return os;
	}

	public JsonObject getTotalDispatchedPendingInRepo(String name) {

		SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_TOTAL_D_P, name);

		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("name", name);

		rs.next();
		job.add("pending", rs.getString("count"));
		
		rs.next();
		job.add("dispatched", rs.getString("count"));

		return job.build();
	}
}
