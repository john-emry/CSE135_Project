<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <style>
        .center {
            text-align: center;
            margin: auto;
            padding: 10px;
        }
    </style>
    <link rel="stylesheet" href="css/bootstrap.css">
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
<div class="center">
    <h2>Login</h2>
    <form action="/Servlet?func=login" method="post">
        username: <input type="text" name="username" style="text-align: center"/></br/>
        <!--password: <input type="password" name="password" style="text-align: center"/><br/>-->
        <input type="submit" value="Login"/>
    </form>
    <form action="signup.jsp" method="get">
        <input type="submit" value="Sign Up"/>
    </form>

</div>
</body>
</html>