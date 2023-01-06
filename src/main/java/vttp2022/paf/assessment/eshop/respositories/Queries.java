package vttp2022.paf.assessment.eshop.respositories;

public class Queries {
    
    public static final String SQL_SELECT_FIND_USER = "select count(*) as count, name, address, email from customers where name = ?";
    public static final String SQL_INSERT_ORDER = "insert into eshop.order(order_id, name, order_date) values (?, ?, ?)";
    public static final String SQL_INSERT_LINEITEM = "insert into eshop.lineitem(item, quantity, order_id) values(?, ?, ?)";
    public static final String SQL_INSERT_ORDER_STATUS = "insert into eshop.order_status(order_id, delivery_id, status, status_update) values(?, ?, ?, ?)";
}
