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
    <h2 class="center"><%= request.getParameter("username") %></h2>

    <%--Update this section so that admins see everuthing but  checkout page, users do not see categories and products--%>
    <a href="/Servlet?func=Categories" class="button">Categories</a>
    <a href="/Servlet?func=Products" class="button">Products</a>
    <a href="/Servlet?func=ProductsBrowsing" class="button">ProductsBrowsing</a>
    <a href="/Servlet?func=ProductsOrder" class="button">Categories</a>
    <a href="/Servlet?func=Checkout" class="button">Check out</a>

    <%--Reload the page when you select a category--%>
    <form action="/Servlet?func=Products" method="post" id="catForm">
    <table class="table" style="float:left">
        <tr>
            <td>Categories</td>
        </tr>
        <c:forEach items="${categoriesList}" var="category">
        <tr>
            <td><button type="submit" form="catForm" value ="${category.id}" name="Category">${category.name}</button></td>
        </tr>
        </c:forEach>
    </table>
    </form>

    <form action="/Servlet?func=Products" method="post" id="searchAndUpdate">
    <table class="table" style="float:left; text-align:center;">
        <tr>
            <td colspan="100">Search:&nbsp;&nbsp;<input type="text" name="searchField" style="width:200px;"/>&nbsp;<button type="submit" form="searchAndUpdate" >Search</button></td>
        </tr>
        <tr>
            <td>Current Category <%= request.getParameter("currentCategory") %>  </td>            
        </tr>
        <!--Suppose ${list} points to a List<Object>, then the following-->
        <tr>
            <td>Product List</td>
        </tr>
        <c:forEach items="${productList}" var="product">
            <tr>
                <td>>SKU:&nbsp;<input type="text" name="productSKU" value="${product.sku}"/>Name:&nbsp;<input type="text" name="productName" value="${product.name}"/> Price:&nbsp;<input type="text" name="productPrice" value="${product.price}"/> <button type="submit" value="${product.productID}" name="ProductUpdate" form="searchAndUpdate" >Update</button><button type="submit" value="${product.productID}" name="ProductDelete" form="searchAndUpdate" >Delete</button></td>
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
                <td>SKU: <input name="newSKU" type="text"/> Price: <input name="newPrice" type="text"/> Name: <input name="newName" type="text"/><button type="submit" value="newProduct" name="ProductAdd">Add</button></td>
            </tr>

            <select name="CategorySelect">
            <c:forEach items="${categoriesList}" var="category">
                <option value="${category.id}">${category.name}</option>
            </c:forEach>
            </select>
    </table>
    </form>
    
</div>
</body>
</html>