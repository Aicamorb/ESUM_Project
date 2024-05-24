package com.esum.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.esum.service.MyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.esum.datalayer.DataLayer;
import com.esum.general.Product;


@RestController
@RequestMapping("/")
public class Controller {
	
	private MyService service;
	
	private void init()
	{
		service = new MyService();
	}
    


	@PostMapping("postProduct")
	public String postProduct(@RequestParam("pID") String pID,@RequestParam("pName") String pName,@RequestParam("pPrice") int pPrice,@RequestParam("pCount") int pCount)
	{
		this.init();
		//Prevent XSS
		pID=HtmlUtils.htmlEscape(pID);
		pName=HtmlUtils.htmlEscape(pName);

		Product product = new Product(pID,pName,pPrice,pCount);
		if(this.service.newProduct(product)==DataLayer.ConnectStatus.SUCCESS)
			return  this.getHead()+alertAndLoc("商品新增成功!","/createProduct")+this.getFoot();
		return  this.getHead()+alertAndLoc("商品新增失敗 請檢察ID是否重複","/createProduct")+this.getFoot();
	}
	@RequestMapping("getProductList")
	public String getProductList()
	{
		this.init();	
		String ret = this.getHead() +this.getProductTable(this.service.getProductList())+this.getFoot();
		return ret;
	}
    @GetMapping("createProduct")
    public String createProduct()
    {
    	init();
    	String ret = this.getHead()+this.getNewProductForm()+this.getFoot();

        return ret;
    }
    
    
 
    @PostMapping("/submits")
    public String submitForm(@RequestParam("MemberID") String MemberID,HttpSession session) {
    	init();
    	//Prevent XSS
    	MemberID=HtmlUtils.htmlEscape(MemberID);
		
    	DataLayer.ConnectStatus dbc =  this.service.createOrder(MemberID,(String) session.getAttribute("pIDs"),(String) session.getAttribute("sq"), Integer.parseInt((String)session.getAttribute("pTotal")));
    	this.service.removeSession(session);
    	if(dbc == DataLayer.ConnectStatus .SUCCESS)
    		return  this.getHead()+this.alertAndLoc("訂單新增成功","/getProductList")+this.getFoot();
    	else if(dbc == DataLayer.ConnectStatus.LOGIC_ERROR)
    		return  this.getHead()+this.alertAndLoc("訂單新增失敗，產品數量不足","/getProductList")+this.getFoot();
    	return  this.getHead()+this.alertAndLoc("訂單新增失敗","/getProductList")+this.getFoot();
    }
    
    @PostMapping("/orderConfirmation")
    @ResponseBody 
    public String orderConfirmation(HttpServletRequest request,HttpSession session) {
    	init();
    	
    	if(null == request.getParameterValues("selectID"))
    		return  this.getHead()+this.alertAndLoc("請至少選擇一筆產品","/getProductList")+this.getFoot();
    	String[] selectIDs = request.getParameterValues("selectID");
    	Map<String,Integer> selectQ=new HashMap<>();
        for (String option : selectIDs) {
        	//Prevent XSS
       	selectQ.put(option,Integer.parseInt(HtmlUtils.htmlEscape(request.getParameter("n_"+option))));
       }
       ArrayList<Product> products = this.service.getProductSelect(selectIDs);
       int totalAmount = this.service.getTotalAmount(products, selectQ);
	   String ret = this.getHead() +this.getProductSelectTable(products, selectQ,totalAmount) +this.getFoot();
	   this.service.setProductSelectSession(selectIDs, selectQ,session);
	   return ret;
    }
	private String alertAndLoc(String text,String url) 
	{
		String ret = "<script>\r\n"
				+ "  alert(\""+text+"\");\r\n"
				+ "  window.location.href ='"+url+"'\r\n"
				+ "</script>\r\n";
		return ret;
	}

	private String getHead()
	{
		return "<!DOCTYPE html>\r\n"
				+ "<html lang=\"en\">\r\n"
				+ "<head>\r\n"
				+ "  <meta charset=\"UTF-8\">\r\n"
				+ "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
				+ "  <title></title>\r\n"
				+ "   <script src=\"https://cdn.jsdelivr.net/npm/vue@2\"></script>\r\n"
				+ "   <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">"
				+ "<style>\r\n"
				+ "    .centered-content {\r\n"
				+ "      display: flex;\r\n"
				+ "      justify-content: center;\r\n"
				+ "      align-items: center;\r\n"
				+ "      height: 50vh;\r\n"
				+ "    }"
				+ "</style></head>\r\n"
				+ "<body>" + this.getNavBar();

	};
	private String getNavBar()
	{
		return 	"<div id=\"myNav\">\r\n"
				+ "        <nav class=\"navbar navbar-expand-lg navbar-dark bg-dark\">\r\n"
				+ "            <div class=\"container\">\r\n"
				+ "                <a class=\"navbar-brand\" href=\"#\">E Sun</a>\r\n"
				+ "                <button class=\"navbar-toggler\" type=\"button\" data-toggle=\"collapse\" data-target=\"#navbarNav\" aria-controls=\"navbarNav\" aria-expanded=\"false\" aria-label=\"Toggle navigation\">\r\n"
				+ "                    <span class=\"navbar-toggler-icon\"></span>\r\n"
				+ "                </button>\r\n"
				+ "                <div class=\"collapse navbar-collapse\" id=\"navbarNav\">\r\n"
				+ "                    <ul class=\"navbar-nav\">\r\n"
				+ "                        <li class=\"nav-item \" v-for=\"(item, index) in navbarItems\" :key=\"index\">\r\n"
				+ "                            <a class=\"nav-link .ext-light\" :href=\"item.url\">{{ item.label }}</a>\r\n"
				+ "                        </li>\r\n"
				+ "                    </ul>\r\n"
				+ "                </div>\r\n"
				+ "            </div>\r\n"
				+ "        </nav>\r\n"
				+ "    </div>"
				+ "        <div class=\"container\">\r\n"
				+ "          <div class=\"row centered-content\">\r\n"
				+ "            <div class=\"col-md-6\">";
	}
	private String getFoot()
	{
		return  "    </div>\r\n"
				+ "  </div>\r\n"
				+ "</div>"
				+"</body>\r\n"+"<script>\r\n"
				+ "        new Vue({\r\n"
				+ "            el: '#myNav',\r\n"
				+ "            data: {\r\n"
				+ "                navbarItems: [\r\n"
				+ "                    { label: 'Create Order', url: '/getProductList' },\r\n"
				+ "                    { label: 'Create Product', url: '/createProduct' }\r\n"
				+ "                ]\r\n"
				+ "            }\r\n"
				+ "        });\r\n"
				+ "    </script>"

				+ "</html>";
	}
	private String getNewProductForm()
	{    	
		String ret =  "  <div class=\"container\">\r\n"
				+ "    <div class=\"row centered-content\">\r\n"
				+ "      <div class=\"col-md-6\">\r\n"
				+ "        <form id=\"myForm\"  method=\"post\" action=\"postProduct\">\r\n"
				+ "          <div class=\"form-group\">\r\n"
				+ "            <label  >{{id}}</label>\r\n"
				+ "            <input name=\"pID\" type=\"text\" maxlength=\"4\" name=\"name\" class=\"form-control\" required>\r\n"
				+ "          </div>\r\n"
				+ "          <div class=\"form-group\">\r\n"
				+ "            <label >{{name}}</label>\r\n"
				+ "            <input name=\"pName\" type=\"text\" maxlength=\"10\" class=\"form-control\"  required>  \r\n"
				+ "          </div>\r\n"
				+ "          <div class=\"form-group\">\r\n"
				+ "            <label >{{price}}</label>\r\n"
				+ "            <input name=\"pPrice\" class=\"form-control\" required></input>\r\n"
				+ "          </div>\r\n"
				+ "          <div class=\"form-group\">\r\n"
				+ "            <label >{{inStock}}</label>\r\n"
				+ "            <input name=\"pCount\"  class=\"form-control\" required></input>\r\n"
				+ "          </div>\r\n"
				+ "          <div class=\"row justify-content-center  mt-6\">\r\n"
				+ "            <div class=\"align-middle\">\r\n"
				+ "              <button type=\"submit\" class=\"btn btn-primary\">Submit</button>\r\n"
				+ "            </div>\r\n"
				+ "          </div>"
				+ "        </form>\r\n"
				+ "      </div>\r\n"
				+ "    </div>\r\n"
				+ "  </div>"
		+ "  <script>\r\n"
		+ "        new Vue({\r\n"
		+ "            el: '#myForm',\r\n"
		+ "            data: { id: '[商品編號]',name:'[商品名稱]',price:'[商品價格]',inStock:'[庫存]' }\r\n"
		+ "        });\r\n"
		+ "  </script>\r\n";
	
		return  ret;
	}
	
	private String getProductTable(ArrayList<Product> products)
	{
		String ret= "  <form method=\"post\" action=\"http://localhost:8080/orderConfirmation\">"
				+ "<table id='myTable' class=\"table mt-4\">\r\n"
				+ "        <tr>\r\n"
				+ "          <th>{{isBuy}}</th>\r\n"
				+ "          <th>{{name}}</th>\r\n"
				+ "          <th>{{price}}</th>\r\n"
				+ "          <th>{{count}}</th>\r\n"
				+ "        </tr>";
		for(int i =0;i<products.size();i++)
		{
			ret=ret+"<tr>"+
		"<td>"+"<input type=\"checkbox\" name=\"selectID\" value=\""+products.get(i).productID+"\">"+"</td>"+
		"<td>"+products.get(i).productName+"</td>"+
		"<td>"+products.get(i).price+"</td>"+
		"<td>"+"<input type=\"number\"  name=\"n_"+products.get(i).productID+"\" min=\"1\" value=\"1\" max=\""+products.get(i).quantity+"\" />"+"</td>"+"</tr>";
		}
		ret=ret+"</table>"
				+ "          <div class=\"row justify-content-center  mt-6\">\r\n"
				+ "            <div class=\"align-middle\">\r\n"
				+ "              <button type=\"submit\" class=\"btn btn-primary\">Submit</button>\r\n"
				+ "            </div>\r\n"
				+ "          </div>"
				+ "</form>"
				+" <script>\r\n"
				+ "        new Vue({\r\n"
				+ "            el: '#myTable',\r\n"
				+ "            data: { isBuy: '是否購買',name:'展品名稱',price:'單價',count:'購入數' }\r\n"
				+ "        });\r\n"
				+ "  </script>\r\n"
				+ "    </div>";
		return ret;
	}
	
	private String getProductSelectTable(ArrayList<Product> products,Map<String,Integer> selectQuantity,int totalAmount)
	{
		String ret= "              <br>\r\n"
				+ "              <div class=\"row justify-content-center mt-6\">\r\n"
				+ "                <div class=\"align-middle\">\r\n"
				+ "                  <h2>訂單確認</h2>\r\n"
				+ "                </div>\r\n"
				+ "              </div>" 
				+ "<form method=\"post\" action=\"/submits\"><table class=\"table mt-4\">\r\n"
				+ "<tr>\r\n"
				+ "          <th>展品名稱</th>\r\n"
				+ "          <th>單價</th>\r\n"
				+ "          <th>購入數</th>\r\n"
				+ "          <th>金額</th>\r\n"
				+ "        </tr>";
		for(int i =0;i<products.size();i++)
		{
			ret=ret+"<tr>"+
		"<td>"+products.get(i).productName+"</td>"+
		"<td>"+products.get(i).price+"</td>"+
		"<td>"+selectQuantity.get(products.get(i).productID)+"</td>"+
		"<td>"+products.get(i).price *Integer.parseInt(selectQuantity.get(products.get(i).productID).toString()) +"</td>"+"</tr>";
		}
		ret=ret+"</table>";
				
				//+ "<br>使用者ID:<input type=\"text\"  name=\"MemberID\" required /><button type=\"submit\" class=\"btn btn-primary\">Submit</button></form>";
		ret = ret+"          <div class=\"row justify-content-center  mt-6\">\r\n"
				+ "            <div class=\"align-middle\">\r\n"
				+ "              使用者ID:<input type=\"text\" name=\"MemberID\" required />\r\n"
				+ "            </div>\r\n"
				+ "          </div><br>\r\n"
				+ "          <div class=\"row justify-content-center mt-6\">\r\n"
				+ "            <div class=\"align-middle\">\r\n"
				+ "              總金額:"+totalAmount+"\r\n"
				+ "            </div>\r\n"
				+ "          </div><br>\r\n"
				+ "          <div class=\"row justify-content-center  mt-6\">\r\n"
				+ "            <div class=\"align-middle\">\r\n"
				+ "              <button type=\"submit\" class=\"btn btn-primary\">Submit</button>\r\n"
				+ "            </div>\r\n"
				+ "          </div></form>";
		return ret;
	}
}