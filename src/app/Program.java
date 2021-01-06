package app;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
	
		Statement st = conn.createStatement();
			
		ResultSet rs = st.executeQuery("SELECT * FROM tb_order "
				+ "INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id "
				+ "INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");
		
		Map<Long, Order> mapOrders = new HashMap<>();
		Map<Long, Product> mapProds = new HashMap<>();
			
		while (rs.next()) {
			
			Long orderId = rs.getLong("order_id");
			if (mapOrders.get(orderId) == null) {
				Order o = instantiateOrder(rs);
				mapOrders.put(orderId, o);
			}
			
			Long productId = rs.getLong("product_id");
			if(mapProds.get(productId) == null) {
				Product p = instantiateProduct(rs);
				mapProds.put(productId, p);
			}
			
			//Magic! Cabuloso!
			mapOrders.get(orderId).getProducts().add(mapProds.get(productId));
		}
		
		for (Long orderId : mapOrders.keySet() ) {
			System.out.println(mapOrders.get(orderId));
			for (Product p : mapOrders.get(orderId).getProducts()) {
				System.out.println(p);
			}
			System.out.println();
		}
	}
	
	private static Order instantiateOrder(ResultSet rs) throws SQLException {
		Order o = new Order();
		o.setId(rs.getLong("order_id"));
		o.setLatitude(rs.getDouble("latitude"));
		o.setLongitude(rs.getDouble("longitude"));
		o.setMoment(rs.getTimestamp("moment").toInstant());
		o.setStatus(OrderStatus.values()[rs.getInt("status")]);
		return o;
	}
	
	private static Product instantiateProduct(ResultSet rs) throws SQLException {
		Product p = new Product();
		p.setId(rs.getLong("product_id"));
		p.setDescription(rs.getString("description"));
		p.setName(rs.getString("name"));
		p.setImageUri(rs.getString("image_uri"));
		p.setPrice(rs.getDouble("price"));
		
		return p;
	}
}
