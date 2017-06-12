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

        .purple {
            color: purple;
        }
        .red {
            color: red;
        }

    </style>
    <script type="text/javascript">

        var jsonGrid = JSON.parse('{}');

        function refreshGrid(){
            //Adjust url for jsp page, how will we show what rows aer updated?
            debugger;
            $.get("/Servlet?func=refresh", function(responseJson) {
                console.log( "Data loaded: " + responseJson );


                //Iterate on json
                $.each(responseJson, function(index, obj) {
                    console.log(obj["type"]);
                    //Product name change / delete.
                    //Should only be on row header right?
                    if(obj["type"] == "p"){
                        //Get the col number of the object that 
                        var colNumber = $("#" + obj["id"]).parent().children().index($("#" + obj["id"]));

                        $("tr td:nth-child(" + colNumber + ")").addClass("purple");
                    }
                    else{
                        //Just color the table cell
                        $("#"+obj.id).addClass("red");
                    }
                });
            });
            
        }

        $(document).ready(function() {
            
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
    <h2 class="center">Sales Analytics V2</h2>
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
                out.println("<a href = \"/Servlet?func=SimilarProducts\" class=\"button\" > Similar Products </a >  ");
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
            <td style="width: 400px;">Category Filter: <%= request.getSession().getAttribute("category")%></td>
        </tr>
    </table>
    <br/>
    <button type="submit" name="updateViewSettings" form="salesAnalyticsForm" >Run</button>
    <br/>
    <br/>
    <button type="button" onclick="refreshGrid();" name="updateViewSettings" form="salesAnalyticsForm" >Refresh</button>
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
    </form>
    
</div>
</body>
</html>