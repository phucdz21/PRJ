<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Access Denied</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container" style="max-width:500px;margin-top:100px;text-align:center">
    <div class="card">
        <h2>⛔ Access Denied</h2>
        <p class="alert alert-error">${errorMsg}</p>
        <a href="${pageContext.request.contextPath}/students" class="btn btn-primary">Go to Home</a>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Logout</a>
    </div>
</div>
</body>
</html>
