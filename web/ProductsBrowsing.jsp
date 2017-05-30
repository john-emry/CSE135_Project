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
    <title>Products browsing</title>
</head>
<body>
<div>
    <h2 class="center">Products Browsing</h2>
    <h2 class="center"><%= request.getSession().getAttribute("Username") %></h2>

    <%--Update this section so that admins see everuthing but  checkout page, users do not see categories and products--%>
    <%
        try {
            if (request.getSession().getAttribute("Role").equals("")) {
                throw new Exception();
            }
            if (request.getSession().getAttribute("Role").equals("Owner")) {
                out.println("<a href = \"/Servlet?func=Categories\" class=\"button\" > Categories </a >");
                out.println("<a href = \"/Servlet?func=Products\" class=\"button\" > Products </a >");
                out.println("<a href = \"/Servlet?func=SalesAnalytics\" class=\"button\" > Sales Analytics </a >");
            } else {
                out.println("<a href=\"/Servlet?func=Checkout\" class=\"button\">Buy Shopping Cart</a>");
            }
        } catch (Exception e) {
        e.printStackTrace();
        request.getRequestDispatcher("/Servlet?func=NotLoggedIn").forward(request, response);
        }
    %>

    <%--Reload the page when you select a category--%>
    <form action="/Servlet?func=ProductsBrowsing" method="post" id="catForm">
    <table class="table" style="float:left">
        <tr>
            <td>Categories</td>
        </tr>
        <c:forEach items="${categoriesList}" var="category">
        <tr>
            <td><button type="submit" form="catForm" value ="${category['id']}" name="Category">${category['name']}</button></td>
        </tr>
        </c:forEach>
    </table>
    </form>

    <form action="/Servlet?func=ProductsBrowsing" method="post" id="productBrowsing">
    <table class="table" style="float:left; text-align:center;">
        <tr>
            <td colspan="100">Search:&nbsp;&nbsp;<input type="text" name="searchField" style="width:200px;"/>&nbsp;<button type="submit" name="browsingSearch" form="productBrowsing" >Search</button></td>
        </tr>
        <tr>
            <td>Current Category <%= request.getAttribute("currentCategory") %>  </td>
        </tr>
        <!--Suppose ${list} points to a List<Object>, then the following-->
        <tr>
            <td>Product List</td>
        </tr>
        <c:forEach items="${productList}" var="product">
            <tr>
                <td>SKU:&nbsp;${product['sku']}&nbsp;Name:&nbsp;${product['name']}&nbsp;Price:&nbsp;${product['price']}&nbsp;<button type="submit" form="productBrowsing" value="${product['productID']}" name="ProductBuy">Order Product</button>
                </td>
            </tr>
        </c:forEach>
        
    </table>
    </form>
    
</div>
</body>
</html>