<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="login-container">
        <h2>Login</h2>
        <form method="post" action="${pageContext.request.contextPath}/login">
            <input type="text" name="username" placeholder="Username" required><br>
            <input type="password" name="password" placeholder="Password" required><br>
            <button type="submit">Sign In</button>
        </form>
        <p class="error">${requestScope.error}</p>
    </div>
</body>
</html>
