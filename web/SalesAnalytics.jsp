<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.*" %>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
    <style>
        body {
            font: 12px helvetica;
        }
        .center {
            text-align: center;
            margin: auto;
            left: 10px;
            right: 10px;
            top: 10px;
            bottom: 10px;
            padding: 10px;
        }

        table.displayTable, tr.displayTable, td.displayTable {
            border: 1px solid black;
            border-collapse: collapse;
            min-width: 30px;
            min-height: 20px;
        }

    </style>
    <script type="text/javascript">
        var noMoreProducts = <%= request.getAttribute("noMoreProducts") %>;
        var noMoreRows = <%= request.getAttribute("noMoreRows") %>;
        var onFirstPage = <%= request.getAttribute("onFirstPage") %>;

        $(document).ready(function() {
            var colCount = 0;
            
            $('table.displaytable tr:nth-child(1) td').each(function () {
                if ($(this).attr('colspan')) {
                    colCount += +$(this).attr('colspan');
                } else {
                    colCount++;
                }
            });

            var tableRows = $('#displaytable tr').length;
            

            console.log(colCount);
            console.log(tableRows);
            
            if(onFirstPage == true){
                //Hide the options table if we are not on the first page
                $("#optionsTable").hide();
            }            
            if(colCount < 11){
                $("#next10button").hide();
            }
            if (tableRows < 21) {
                $("#next20button").hide();
            }
            //Bold first col of the display table
            $("#displayTable").find("td:first-child").css("font-weight", "bold");
            //Bold the first row (col headers)
            $("#displayTable tr:first").css("font-weight","bold");
            
        });

    </script>
    <meta charset="UTF-8">
    <title>Sales Analytics</title>
</head>
<body>
<div>
    <h2 class="center">Sales Analytics</h2>
    <h2 class="center">Hello:&nbsp;<%= request.getSession().getAttribute("Username") %></h2>

    <%--Update this section so that admins see everything but checkout page, users do not see categories and products--%>
    <%
        try {
            if (request.getSession().getAttribute("Role").equals("")) {
                throw new Exception();
            }
            if (request.getSession().getAttribute("Role").equals("Owner")) {
                out.println("<a href = \"/Servlet?func=Categories\" class=\"button\" > Categories </a >  ");
                out.println("<a href = \"/Servlet?func=Products\" class=\"button\" > Products </a >  ");
            } else {
                out.println("<a href=\"/Servlet?func=Checkout\" class=\"button\"> Buy Shopping Cart </a>  ");
                throw new Exception("Owners Only");
            }
            out.println("<a href=\"/Servlet?func=ProductsBrowsing\" class=\"button\"> Products Browsing </a>  ");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().equals("Owners Only")) {
                request.getRequestDispatcher("/Servlet?func=OwnersOnly").forward(request, response);
            } else {
                request.getRequestDispatcher("/Servlet?func=NotLoggedIn").forward(request, response);
            }
        }
    %>

  
    <form action="/Servlet?func=SalesAnalytics" method="post"  id="salesAnalyticsForm">
    <table class="optionsTable" id="optionsTable">
        <tr>
            <td style="width: 200px;">Row settings:</td>
            <td style="width: 200px;">Order settings:</td>
        </tr>
        <tr>
            <td>
                <select name="rowSelect" id="rowSelect">
                    <option value="customer" default>Customers</option>
                    <option value="state">States</option>
                </select>
            </td>
            <td>
                <select name="orderSelect" id="orderSelect">
                    <option value="alphabetical" default>Alphabetical</option>
                    <option value="topk">Top-K</option>
                </select>
            </td>
            
        </tr>
        <tr>
            <td style="width: 200px;" colspan="3">Sales Filtering</td>
        </tr>
        <tr>
            <td style="width: 200px;">Product Category Filter</td>
        </tr>
        <tr>
            <td>
                <select name="productCategoryFilter" id="productCategoryFilter">
                    <option value="allCategories" default>All Categories</option>
                <c:forEach items="${categoriesList}" var="cList">
                    <option value="${cList['name']}">${cList['name']}</option>
                </c:forEach>
                </select>
            </td>
        </tr>
    </table>
    <table class="optionsTable">
        <tr>
            <td style="width: 200px;">Row Type: <%= request.getSession().getAttribute("row")%></td>
            <td style="width: 200px;">Order Type: <%= request.getSession().getAttribute("order")%></td>
            <td style="width: 400px;">Category Filter: <%= request.getSession().getAttribute("category")%></td>
        </tr>
    </table>
    <br/>
    <button type="submit" name="updateViewSettings" form="salesAnalyticsForm" >Run Query</button>
    <br/>
        <br/>

    <table class="displayTable" id="displayTable">
        <c:forEach items="${displayTableRows}" var="displayTable">
            <tr class="displayTable">
                <c:forEach items="${displayTable}" var="columnVal">
                    <td class="displayTable">${columnVal}</td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>
        <br/>
        <br/>

    <%--Buttons down here to submit the form: next buttons over the report--%>
    <button type="submit" name="next20button" id="next20button" value="clicked" form="salesAnalyticsForm">Next 20 <%= request.getAttribute("custOrState") %></button>
        <br/>
        <br/>
    <button type="submit" name="next10button" id="next10button" value="clicked" form="salesAnalyticsForm">Next 10 Products</button>

    </form>
    
</div>
</body>
</html>