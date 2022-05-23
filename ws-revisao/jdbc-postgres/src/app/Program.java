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
	
		Statement st = conn.createStatement();//statement é o comando sql q vou enviar bara o bd
			
		ResultSet rs = st.executeQuery("SELECT * FROM tb_order "
				+ "INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id "
				+ "INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");//resultSet é o resultado de consulta
			
		
		Map<Long, Order> map = new HashMap<>(); //colecao de pares chave valor, nesse caso armazena os objetos do tp order e impede que criem-se order com id repetido
		Map<Long, Product> prods = new HashMap<>();
		while (rs.next()) {
			
			//testa se o order ja existe naquele map, ou seja, só irei instanciar o pedido se ele não existe dentro do meu map
			Long orderId = rs.getLong("order_id");
			if(map.get(orderId) == null) {
				Order order = instantiateOrder(rs);
				map.put(orderId, order);
			}
			
			Long productId = rs.getLong("product_id");
			if(map.get(productId) == null) {
				Product p = instantiateProduct(rs);
				prods.put(productId, p);
			}
			
			
			//metodo para acessar a lista de produtos e o insere nela
			//1º pega o pedido, 2º pega lista de produtos dele, 3º adiciona na lista o produto que acabou de registrar no map
			map.get(orderId).getProducts().add(prods.get(productId));
			
			
		}
		
		for(Long orderId : map.keySet()) {
			System.out.println(map.get(orderId));
			for(Product p : map.get(orderId).getProducts()) {
				System.out.println(p);
			}
			System.out.println();
		}
		
	}
	
	
	private static Order instantiateOrder(ResultSet rs) throws SQLException{
		Order order = new Order();
		order.setId(rs.getLong("order_id"));
		order.setLatitude(rs.getDouble("latitude"));
		order.setLongitude(rs.getDouble("longitude"));
		order.setMoment(rs.getTimestamp("moment").toInstant());
		order.setStatus(OrderStatus.values()[rs.getInt("status")]); //para converter para orderStatus
		return order;
	}
	
	private static Product instantiateProduct(ResultSet rs) throws SQLException{
		Product p = new Product();
		p.setId(rs.getLong("product_id"));
		p.setDescription(rs.getString("description"));
		p.setName(rs.getString("Name"));
		p.setImageUri(rs.getString("image_uri"));
		p.setPrice(rs.getDouble("price"));
		return p;
	}
	
}
