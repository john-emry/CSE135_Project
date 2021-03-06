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
    <title>Buy Shopping Cart</title>
</head>
<body>
<div>
    <h2 class="center">Confirmation</h2>
    <h2 class="center"><%= request.getSession().getAttribute("Username") %></h2>

    <%--Update this section so that admins see everrything but  checkout page, users do not see categories and products--%>
    <%
        try {
            if (request.getSession().getAttribute("Role").equals("")) {
                throw new Exception();
            }
            if (request.getSession().getAttribute("Role").equals("Owner")) {
                out.println("<a href = \"/Servlet?func=Categories\" class=\"button\" > Categories </a >");
                out.println("<a href = \"/Servlet?func=Products\" class=\"button\" > Products </a >");
                out.println("<a href = \"/Servlet?func=SalesAnalytics\" class=\"button\" > Sales Analytics </a >");
                out.println("<a href = \"/Servlet?func=SimilarProducts\" class=\"button\" > Similar Products </a >  ");
            }
            out.println("<a href=\"/Servlet?func=ProductsBrowsing\" class=\"button\">Products Browsing</a>");
        } catch (Exception e) {
        e.printStackTrace();
        request.getRequestDispatcher("/Servlet?func=NotLoggedIn").forward(request, response);
        }
    %>

        <%--Shopping cart contents--%>
        <table class="table" style="float:left; text-align:center;">
            <tr>
                <td>Current Shopping Cart</td>
            </tr>
            <c:forEach items="${shoppingCart}" var="item">
                <tr>
                    <td>Name:&nbsp;${item['name']}&nbsp;Price:&nbsp;${item['price']}&nbsp;Quantity:&nbsp;${item['quantity']}</td>
                </tr>
            </c:forEach>

        </table>

</div>
</body>
</html>