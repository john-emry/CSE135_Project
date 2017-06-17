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

        var date = new Date();
        var productIDArr = new Array();
        var refreshIDArr = new Array();
        productIDArr = <%= request.getAttribute("loadArray")%>;
//        console.log(productIDArr);

        function refreshGrid(){
            //Adjust url for jsp page, how will we show what rows aer updated?
                                        
            //Ajax call to refresh the grid, we pass a json object                                        
            $.get("/Servlet?func=refresh&date=" + date.getTime().toString(), function(responseJson) {
//                console.log( "Data loaded: " + responseJson );
                // debugger;
                refreshIDArr = JSON.parse(responseJson["top"]);
                console.log(responseJson);
                //Iterate on json
                $.each(responseJson["grid"], function(index, obj) {
                    // debugger;
                    
                    var id = obj["State"] + obj["PID"];
                    var value = obj["Value"];

                    if(obj["Type"] == "p"){
                        //Get the col number of the object that 
                        var colNumber = $("#" + id).parent().children().index($("#" + id)) + 1;
                        $("tr td:nth-child(" + colNumber + ")").addClass("purple");
                        $("tr td:nth-child(" + colNumber + ")").removeClass("red");

                    }
                    else{
                        //Just color the table cell
                        $("#" + id).addClass("red");
                        var valToAdd = parseInt($("#" + id).text());
                        var valToAdd2 = parseInt(value);
                        var newVal = valToAdd + valToAdd2;
                        $("#" + id).text(newVal);
                    }
                });
                //Iterate over the current product id array, if the id is not found in the new top
                // 50, make that column purple.
                var index = 0;
                console.log(productIDArr);
                console.log(refreshIDArr);
                if(refreshIDArr.length > 0) {
                    for (index = 0; index < productIDArr.length; ++index) {
                        var val = productIDArr[index];

                        if ($.inArray(val, refreshIDArr) == -1) {
                            console.log("Make purple: " + val);
                            var colNumber = $("#" + val).parent().children().index($("#" + val)) + 1;
                            $("tr td:nth-child(" + colNumber + ")").addClass("purple");
                            $("tr td:nth-child(" + colNumber + ")").removeClass("red");
                            //make purple, we have found a product that is no longer in the top 50
                            //remove red, add purple
                        }
                    }
                }
                $("#itemsChanged").text("")
                if(refreshIDArr.length > 0) {
                    $("#itemsChanged").text("Top 50 products changed, items no longer in top 50: ");
                    for (index = 0; index < refreshIDArr.length; ++index) {
                        var val = refreshIDArr[index];

                        if ($.inArray(val, productIDArr) == -1) {
                            val = parseInt(val) -1;
                            var currentText = $("#itemsChanged").text();
                            $("#itemsChanged").text(currentText + val + ", ")
                            //make purple, we have found a product that is no longer in the top 50
                            //remove red, add purple
                        }
                    }
                }

            });

            date = new Date();
            
        }


        function buyOrdersFastScript(){
            //Adjust url for jsp page, how will we show what rows aer updated?
            var numOrders = $("#numOrders").val();
            //Ajax call to refresh the grid, we pass a json object

            $.get("/Servlet?func=buyOrders&numOrders=" + numOrders, function(responseJson) {
               
            });
            
        }


        $(document).ready(function() {

            date = new Date();
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
    <%--Added to buy orders--%>
    <button type="button" onclick="buyOrdersFastScript();" name="buyOrders">Buy Orders</button>
    <input type="number" style="width:300px;" id="numOrders"/>
    <br/>
    <br/>
        <p id="itemsChanged"></p>
        <br/>
        <br/>
    <table class="displayTable" id="displayTable">
        <c:forEach items="${displayTableRows}" var="displayTable">
            <tr class="displayTable">
                <c:forEach items="${displayTable}" var="columnVal">
                    <td class="displayTable" id="${columnVal['id']}" value="${columnVal['value']}"}">${columnVal['value']}</td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>
    </form>
    
</div>
</body>
</html>