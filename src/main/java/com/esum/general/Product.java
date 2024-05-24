package com.esum.general;

public class Product {
	public String productID;
	public String productName;
    public int price;
    public int quantity; 
    public int selectQuantity;
	public Product( String productID,String productName,int price,int quantity)
	{
		this.productID=productID;
		this.productName=productName;
		this.price=price;
		this.quantity=quantity; 
		
	}

    
}
