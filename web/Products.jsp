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
    <link rel="stylesheet" href="css/bootstrap.css">
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
<div>
    <h2 class="center">Products</h2>
    <h2 class="center"><%= request.getSession().getAttribute("Username") %></h2>

    <%--Update this section so that admins see everything but checkout page, users do not see categories and products--%>
    <%
        try {
            if (request.getSession().getAttribute("Role").equals("")) {
                throw new Exception();
            }
            if (request.getSession().getAttribute("Role").equals("Owner")) {
                out.println("<a href = \"/Servlet?func=Categories\" class=\"button\" > Categories </a >  ");
            } else {
                throw new Exception("Owners Only");
                out.println("<a href=\"/Servlet?func=Checkout\" class=\"button\">Buy Shopping Cart</a>  ");
            }
            out.println("<a href=\"/Servlet?func=ProductsBrowsing\" class=\"button\">Products Browsing</a>  ");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Owners Only")) {
                request.getRequestDispatcher("/Servlet?func=OwnersOnly").forward(request, response);
            } else {
                request.getRequestDispatcher("/Servlet?func=NotLoggedIn").forward(request, response);
            }
        }
    %>

    <%--Reload the page when you select a category--%>
    <form action="/Servlet?func=Products" method="post" id="catForm">
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

    <form action="/Servlet?func=Products" method="post" id="searchAndUpdate">
    <table class="table" style="float:left; text-align:center;">
        <tr>
            <td colspan="100">Search:&nbsp;&nbsp;<input type="text" name="searchField" style="width:200px;"/>&nbsp;<button type="submit" name="productsSearch" form="searchAndUpdate" >Search</button></td>
        </tr>
        <tr>
            <td>Current Category <%= request.getAttribute("currentCategory") %>  </td>
        </tr>
        <!--Suppose ${list} points to a List<Object>, then the following-->
        <tr>
            <td>Product List</td>
        </tr>
        <tr>
            <%
                try {
                    String s = request.getAttribute("errorMessage").toString();
                    if (s != null && !s.equals("")) {
                        out.println(s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            %>
        </tr>
        <c:forEach items="${productList}" var="product">
            <tr>
                <td>SKU:&nbsp;<input type="text" name="productSKU" value="${product['sku']}"/>Name:&nbsp;<input type="text" name="productName" value="${product['name']}"/> Price:&nbsp;<input type="text" name="productPrice" value="${product['price']}"/> <button type="submit" value="${product['productID']}" name="ProductUpdate" form="searchAndUpdate" >Update</button><button type="submit" value="${product.productID}" name="ProductDelete" form="searchAndUpdate" >Delete</button></td>
            </tr>
        </c:forEach>
        
    </table>
    </form>
    <%--Start product add--%>
    <form action="/Servlet?func=Products" method="post"  id="newProduct">
    <table>
        <tr>
                <td>
                    Add a new product!
                </td>
            </tr>
            <tr>
                <td>SKU: <input name="newSKU" type="text"/> Price: <input name="newPrice" type="text"/> Name: <input name="newName" type="text"/></td>
            <td>
                Category: <select name="CategorySelect">
                <c:forEach items="${categoriesDropDown}" var="category">
                    <option value="${category['id']}">${category['name']}</option>
                </c:forEach>
                </select>
                <button type="submit" value="newProduct" name="ProductAdd">Add</button>
            </td>
    </table>
    </form>
    
</div>
</body>
</html>