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
<style>
 /* ========== RESET CƠ BẢN ========== */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: "Segoe UI", Roboto, Arial, sans-serif;
}

/* ========== NỀN TOÀN TRANG (màu cam) ========== */
body {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #f97316, #fb923c); /* cam đậm → cam sáng */
}

/* ========== KHUNG LOGIN ========== */
.login-container {
    width: 350px;
    background-color: #ffffff;
    padding: 40px 30px;
    border-radius: 12px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
    text-align: center;
    animation: fadeIn 0.6s ease-in-out;
}

/* ========== TIÊU ĐỀ ========== */
.login-container h2 {
    margin-bottom: 25px;
    color: #b45309; /* nâu cam đậm */
    font-size: 24px;
    letter-spacing: 0.5px;
}

/* ========== INPUT ========== */
.login-container input[type="text"],
.login-container input[type="password"] {
    width: 100%;
    padding: 10px 12px;
    margin-bottom: 15px;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    font-size: 14px;
    transition: all 0.2s ease;
}

.login-container input:focus {
    border-color: #f97316;
    box-shadow: 0 0 4px rgba(249, 115, 22, 0.4);
    outline: none;
}

/* ========== NÚT SIGN IN ========== */
.login-container button {
    width: 100%;
    background-color: #f97316; /* cam chủ đạo */
    color: #ffffff;
    border: none;
    padding: 10px;
    font-size: 15px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.login-container button:hover {
    background-color: #ea580c; /* cam đậm khi hover */
}

.login-container button:active {
    transform: scale(0.97);
}

/* ========== THÔNG BÁO LỖI ========== */
.error {
    margin-top: 10px;
    color: #ef4444;
    font-size: 13px;
}

/* ========== HIỆU ỨNG ========== */
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(15px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* ========== RESPONSIVE ========== */
@media (max-width: 400px) {
    .login-container {
        width: 90%;
        padding: 30px 20px;
    }

    .login-container h2 {
        font-size: 20px;
    }
}
</style>