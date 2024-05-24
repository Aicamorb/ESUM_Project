package com.esum.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.esum.datalayer.DataLayer;
import com.esum.general.Product;
import org.springframework.stereotype.Service;
import com.esum.general.StringConversion;

import jakarta.servlet.http.HttpSession;

@Service
public class MyService {
	private DataLayer dataLayer;
	
	public MyService()
	{
		this.init();
	}
	private void init()
	{
		 dataLayer= new DataLayer();
	}
	public void removeSession(HttpSession session)
	{
    	session.removeAttribute("sq");
    	session.removeAttribute("pIDs");
    	session.removeAttribute("pTotal");
    	
	}
	public DataLayer.ConnectStatus createOrder(String memberID,String _pIDs,String _sq,int totalAmount)
	{
		String[] pIDs = StringConversion.strDismantle(_pIDs);
		String[] sq = StringConversion.strDismantle(_sq);
		ArrayList<Product> products = this.getProductSelect(pIDs);
		Map<String,Integer> map = new HashMap<>(); 
		for(int i=0;i<pIDs.length;i++)
		{
			map.put(pIDs[i], Integer.parseInt(sq[i]));
		}
		return this.dataLayer.createOrder(products, this.getOrderID(),memberID,totalAmount,map);
		
	}
	
	public DataLayer.ConnectStatus newProduct(Product product)
	{
		return this.dataLayer.newProduct(product.productID,product.productName, product.price, product.quantity);
		
	}
	public ArrayList<Product>  getProductList()
	{
		return dataLayer.getProductInStock();
	}

	public int getTotalAmount(ArrayList<Product> products,Map<String,Integer> selectQuantity)
	{
		return this.calculateTotalAmount(products,selectQuantity);
	}
	public ArrayList<Product> getProductSelect(String[] pIDS)
	{
		return this.dataLayer.getProductSelect(this.idTransfer(pIDS));
	}
	

	public void setProductSelectSession(String[] pIDS,Map<String,Integer> selectQuantity,HttpSession session)
	{
		this.removeSession(session);
    	//session.removeAttribute("pIDs");
		session.setAttribute("pIDs",StringConversion.strCombination(pIDS));
		String[] sq = new String[pIDS.length];
		for(int i=0;i<pIDS.length;i++)
		{
			sq[i] = selectQuantity.get(pIDS[i]).toString();
		}
		//session.removeAttribute("sq");
		session.setAttribute("sq",StringConversion.strCombination(sq));
		var products = this.dataLayer.getProductSelect(this.idTransfer(pIDS));
		int total = this.calculateTotalAmount(products,selectQuantity);
		//session.removeAttribute("pTotal");
		session.setAttribute("pTotal",  ""+total);
	}


	private int calculateTotalAmount(ArrayList<Product> p,Map<String,Integer> q)
	{
		int ret = 0;
		for(int i=0;i<p.size();i++)
		{
			ret = ret+p.get(i).price*q.get(p.get(i).productID);
		}
		return ret;
		
	}
	private String idTransfer(String[] s)
	{
		String ret = "\"'"+s[0]+"'";
		for(int i =1;i<s.length;i++)
		{
			ret=ret+",'"+s[i]+"'";
		}
		return ret+"\"";
		
	}
	private String getOrderID()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmm");
        String ret="Ms"+sdf.format(new Date());
        return ret;
	}
}