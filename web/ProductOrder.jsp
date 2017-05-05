<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.*" %>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <style>
        .center {
            text-align: center;
            margin: auto;
            left: 10px;
            right: 10px;
            top: 10px;
            bottom: 10px;
            padding: 10px;
        }
        .table {
            border: 1px solid blue;
            min-width: 400px;
            min-height: 300px;
        }
    </style>
    <script type="text/javascript">

    </script>
    
    <meta charset="UTF-8">
    <title>Product Order</title>
</head>
<body>
<div>
    <h2 class="center">Product Order</h2>
    <h2 class="center"><%= request.getSession.getAttribute("username") %></h2>

    <%--Update this section so that admins see everrything but  checkout page, users do not see categories and products--%>
    <%
        if (request.getSession().getAttribute("Role").equals("Owner")) {
            out.println("<a href = \"/Servlet?func=Categories\" class=\"button\" > Categories </a >");
            out.println("<a href = \"/Servlet?func=Products\" class=\"button\" > Products </a >");
        } else {
            out.println("<a href=\"/Servlet?func=BuyShoppingCart\" class=\"button\">Buy Shopping Cart</a>");
        }
        out.println("<a href=\"/Servlet?func=ProductsBrowsing\" class=\"button\">Products Browsing</a>");
    %>

    <form action="/Servlet?func=ProductOrder" method="post" id="ProductOrder">
    <%--Shopping cart contents--%>
    <table class="table" style="float:left; text-align:center;">
        <tr>
            <td>Current Shopping Cart</td>      
        </tr>
        <c:forEach items="${shoppingCart}" var="item">
            <tr>
                <td>>SKU:&nbsp;${item['sku']}>&nbsp;Name:&nbsp;${item['name']}&nbsp;Price:&nbsp;${item['price']}&nbsp;Quantity:&nbsp;${item['quantity']}</td>
            </tr>
        </c:forEach>

        <tr>
            <td>Product to buy</td>
        </tr>
        <c:forEach items="${product}" var="product">
            <tr>
                <td>>SKU:&nbsp;${product['sku']}>Name:&nbsp;${product['name']}Price:&nbsp;${product['price']}&nbsp;Quantity:&nbsp;<input style="width:20px;" name="ProductQuantity" type="number" id="ProductQuantity"/> 
                    <button type="submit" value="${product.['productID']}" name="AddToCart" form="ProductOrder" id="AddToCart">Add to cart</button>
                </td>
            </tr>
        </c:forEach>
        
    </table>
    </form>
    
</div>
</body>
</html>