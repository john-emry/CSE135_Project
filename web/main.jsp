<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <style>
        .center {
            text-align: center;
            margin: auto;
            padding: 10px;
        }
        .left {
            text-align: left;
            width: 200px;
            padding: 0px;
            top: 10px;
            border: 3px solid #660000;
        }
        .frame {
            position: absolute;
            padding: 10px;
            left: 210px;
            right: 10px;
            top: 10px;
            bottom: 10px;
            border: 3px solid #009900;
        }
    </style>
    <link rel="stylesheet" href="css/bootstrap.css">
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
<div class="frame">
    <iframe id="iframe" src="Products.jsp" style="width:100%; height: 100%"></iframe>
</div>
<div class="left">
    <script>
        function setURL(url) {
            document.getElementById('iframe').src = url;
        }
    </script>
    <input type="button" onclick="setURL('Categories.jsp')" value="Categories"/><br/>
    <input type="button" onclick="setURL('Products.jsp')" value="Products"/><br/>
    <input type="button" onclick="setURL('ProductsBrowsing.jsp')" value="Products Browsing"/><br/>
    <input type="button" onclick="setURL('ProductOrder.jsp')" value="Product Order"/><br/>
    <input type="button" onclick="setURL('BuyShoppingCart.jsp')" value="Buy Shopping Cart"/><br/>
</div>

</body>
</html>