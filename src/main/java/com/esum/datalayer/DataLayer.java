package com.esum.datalayer;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esum.general.Product;

@RestController
public class DataLayer {
	
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs =null;
	private CallableStatement cs=null;
	public enum ConnectStatus {
	    SUCCESS, DB_ERROR , QUERY_ERROR ,LOGIC_ERROR
	}
	
	private DataLayer.ConnectStatus connectDB()
	{
        try{
            Class.forName(SQLConfig.JDBC_DRIVER);
            conn = DriverManager.getConnection(SQLConfig.DB_URL,SQLConfig.USER,SQLConfig.PASS);
            }
        catch(Exception e){
            e.printStackTrace();
            return ConnectStatus.DB_ERROR;
        }
        return ConnectStatus.SUCCESS;
	}
	
	private DataLayer.ConnectStatus connectDBClose()
	{
		AutoCloseable[] autoCloseable = {rs,cs,stmt,conn};
        try{
        	for(var i=0;i<autoCloseable.length;i++)
        	{
        		if(autoCloseable[i]!=null)
        			autoCloseable[i].close();
        	}
        }
        catch(Exception e){
            e.printStackTrace();
            return ConnectStatus.DB_ERROR;
        }
        return ConnectStatus.SUCCESS;
	}
	
	
	@PostMapping("/POST/newProduct")
	public DataLayer.ConnectStatus newProduct(@RequestParam("pID") String pID,@RequestParam("pName") String pName,@RequestParam("pPrice") int pPrice,@RequestParam("pCount") int pCount)
	{
		connectDB();
	    try {
	    cs = conn.prepareCall("{call newProduct(?,?,?,?)}");
	    cs.setString(1, pID);
	    cs.setString(2, pName);
	    cs.setInt(3, pPrice);
	    cs.setInt(4, pCount);

	    cs.executeQuery();
	    } catch (SQLException e) {
	    	System.out.print(e.toString());
	    	return DataLayer.ConnectStatus.DB_ERROR;
	    }finally {
	    	connectDBClose();
	    }
		return DataLayer.ConnectStatus.SUCCESS;
	}
	
	@PostMapping("/POST/createOrder")
	public DataLayer.ConnectStatus createOrder(@RequestParam("Products") ArrayList<Product> products,@RequestParam("OrderID") String orderID,@RequestParam("memberID") String memberID,@RequestParam("price") int price,@RequestParam("idMap") Map<String,Integer> idMap )
	{
		DataLayer.ConnectStatus ret;
		connectDB();
	    try {
	    //Transaction
	    conn.setAutoCommit(false);

	    cs = conn.prepareCall("{call createOrder(?,?,?)}");
	    cs.setString(1, orderID);
	    cs.setString(2, memberID);
	    cs.setInt(3, price);
	    cs.executeQuery();
	    for(int i=0;i<products.size();i++)
	    {
		    cs = conn.prepareCall("{call createOrderdetail(?,?,?,?)}");
		    cs.setString(1, orderID);
		    cs.setString(2, products.get(i).productID);
		    cs.setInt(3, idMap.get( products.get(i).productID) );
		    cs.setInt(4, products.get(i).price);
		    if(products.get(i).quantity -idMap.get( products.get(i).productID)<0)
		    {
		    	throw new Exception();
		    }
		    cs.executeQuery();
	    }
	    conn.commit();
	    conn.setAutoCommit(true);
	    ret= DataLayer.ConnectStatus.SUCCESS ;
	    } catch (SQLException e) {
	    	try {
				conn.rollback();
				conn.setAutoCommit(true);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
	    	System.out.print(e.toString());
	    	ret=DataLayer.ConnectStatus.DB_ERROR ;
	    }catch(Exception e)
	    {
	    	ret=DataLayer.ConnectStatus.LOGIC_ERROR ;
	    }
	    finally {
	    	connectDBClose();
	    }
		return ret ;
	}
	
	@RequestMapping("/GET/getProductInStock")
	public ArrayList<Product> getProductInStock()
	{
		connectDB();
		ArrayList<Product> products = new ArrayList();
		
	    try {
	    cs = conn.prepareCall("{call getProductInStock()}");
	    rs = cs.executeQuery();    		   		
        while(rs.next()){
        	String productID  = rs.getString("ProductID");
            String productName = rs.getString("ProductName");
            int price = rs.getInt("Price");
            int Quantity = rs.getInt("Quantity");
            products.add(new Product(productID,productName,price,Quantity));
        }
	    } catch (SQLException e) {
	    	System.out.print(e.toString());
	    }
	    finally {
	    	connectDBClose();
	    }
	    return products;
	}
	@RequestMapping("/GET/getProductSelect")
	public ArrayList<Product> getProductSelect(@RequestParam("pID") String pID)
	{
		connectDB();
		ArrayList<Product> products = new ArrayList();
	    try {
	    cs = conn.prepareCall("{call getProductSelect("+pID+")}");
	    rs = cs.executeQuery();    		   		
        while(rs.next()){
        	String productID  = rs.getString("ProductID");
            String productName = rs.getString("ProductName");
            int price = rs.getInt("Price");
            int Quantity = rs.getInt("Quantity");
            products.add(new Product(productID,productName,price,Quantity));
        }
	    } catch (SQLException e) {
	    	System.out.print(e.toString());
	    }finally {
	    	connectDBClose();
	    }
	    return products;
	}

	
}
