<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Department Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">

    <div class="page-header">
        <div>
            <h2>🏢 Department Management</h2>
            <span class="badge badge-manager">Manager</span>
        </div>
        <div class="header-right">
            <span>👤 <strong>${loggedUser.username}</strong></span>
            <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">Students</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Logout</a>
        </div>
    </div>

    <!-- Errors -->
    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul>
                <c:forEach var="err" items="${errors}">
                    <li>${err}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <!-- Form -->
    <div class="card">
        <h3>${not empty editDept ? 'Edit Department' : 'Add Department'}</h3>
        <form method="post" action="${pageContext.request.contextPath}/departments">
            <input type="hidden" name="action" value="${not empty editDept ? 'update' : 'add'}">
            <c:if test="${not empty editDept}">
                <input type="hidden" name="id" value="${editDept.id}">
            </c:if>
            <div class="form-row">
                <div class="form-group">
                    <label>Department Name</label>
                    <input type="text" name="departmentName"
                           value="${not empty editDept ? editDept.departmentName : ''}"
                           placeholder="5–50 characters" required>
                </div>
                <div class="form-actions" style="align-self:flex-end">
                    <button type="submit" class="btn btn-primary">
                        ${not empty editDept ? '💾 Update' : '➕ Add'}
                    </button>
                    <c:if test="${not empty editDept}">
                        <a href="${pageContext.request.contextPath}/departments" class="btn btn-secondary">Cancel</a>
                    </c:if>
                </div>
            </div>
        </form>
    </div>

    <!-- Department List -->
    <div class="card">
        <h3>Department List</h3>
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Department Name</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty departments}">
                        <tr><td colspan="3" style="text-align:center;color:#888;">No departments found.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="dept" items="${departments}" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td>${dept.departmentName}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/departments?action=edit&id=${dept.id}"
                                       class="btn btn-sm btn-secondary">✏️ Edit</a>
                                    <a href="${pageContext.request.contextPath}/departments?action=delete&id=${dept.id}"
                                       class="btn btn-sm btn-danger"
                                       onclick="return confirm('Delete this department?')">🗑️ Delete</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

</div>
</body>
</html>
