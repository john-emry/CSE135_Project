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
            min-width: 400px;
            min-height: 300px;
        }

    </style>
    <script type="text/javascript">
        // var onFirstPage = <%= request.getSession().getAttribute("onFirstPage") %>;
        // var showNextButtons = <%= request.getSession().getAttribute("showNextButtons") %>;

        // $(document).ready(function(){

        //     if(onFirstPage == true){
        //         //Hide the options table if we are not on the first page
        //         $(".optionsTable").hide();
        //     }            
        //     if(showNextButtons == false){
        //         $("#next20button").hide();
        //     }

        //     //Bold first col of the display table
        //     $("#displayTable").find("td:first-child").css("font-weight", "bold");
        //     //Bold the first row (col headers)
        //     $("#displayTable tr:first").css("font-weight","bold");
            
        // });

    </script>
    <meta charset="UTF-8">
    <title>Similar Products</title>
</head>
<body>
<div>
    <h2 class="center">Similar Products</h2>
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

  
    <form action="/Servlet?func=SimilarProducts" method="post"  id="similarProducts">
   
    <table class="displayTable" id="displayTable">
        <c:forEach items="${displayTableRows}" var="displayTable">
            <tr> class="displayTable"
                <c:forEach items="${displayTableColumns}" var="displayTableColumns">
                    <td class="displayTable">${displayTableColumns['colVal']}</td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>

    </form>
    
</div>
</body>
</html>