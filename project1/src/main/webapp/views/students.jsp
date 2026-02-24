<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Management Page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">

    <!-- Header -->
    <div class="page-header">
        <div>
            <h2>🎓 Student Management Page</h2>
            <span class="badge badge-${loggedUser.role == 1 ? 'manager' : 'staff'}">
                ${loggedUser.roleName}
            </span>
        </div>
        <div class="header-right">
            <span>👤 <strong>${loggedUser.username}</strong></span>
            <c:if test="${loggedUser.role == 1}">
                <a href="${pageContext.request.contextPath}/departments" class="btn btn-secondary">Departments</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Logout</a>
        </div>
    </div>

    <!-- Error / Validation messages -->
    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul>
                <c:forEach var="err" items="${errors}">
                    <li>${err}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
    <c:if test="${not empty errorMsg}">
        <div class="alert alert-error">${errorMsg}</div>
    </c:if>

    <!-- Add / Edit Form (Staff only) -->
    <c:if test="${loggedUser.role == 2}">
        <div class="card">
            <h3>${not empty editStudent ? 'Edit Student' : 'Add New Student'}</h3>
            <form method="post" action="${pageContext.request.contextPath}/students">
                <input type="hidden" name="action" value="${not empty editStudent ? 'update' : 'add'}">
                <c:if test="${not empty editStudent}">
                    <input type="hidden" name="id" value="${editStudent.id}">
                </c:if>

                <div class="form-row">
                    <div class="form-group">
                        <label>Student ID</label>
                        <input type="text" name="studentId"
                               value="${not empty editStudent ? editStudent.studentId : ''}"
                               ${not empty editStudent ? 'readonly' : ''}
                               placeholder="e.g. SE001" required>
                    </div>
                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" name="name"
                               value="${not empty editStudent ? editStudent.name : ''}"
                               placeholder="5–50 characters" required>
                    </div>
                    <div class="form-group">
                        <label>GPA</label>
                        <input type="number" name="gpa" step="0.1" min="0" max="10"
                               value="${not empty editStudent ? editStudent.gpa : ''}"
                               placeholder="0.0 – 10.0" required>
                    </div>
                    <div class="form-group">
                        <label>Department</label>
                        <select name="departmentId" required>
                            <option value="">-- Select Department --</option>
                            <c:forEach var="dept" items="${departments}">
                                <option value="${dept.id}"
                                    ${not empty editStudent && editStudent.department.id == dept.id ? 'selected' : ''}>
                                    ${dept.departmentName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        ${not empty editStudent ? '💾 Update' : '➕ Add'}
                    </button>
                    <c:if test="${not empty editStudent}">
                        <a href="${pageContext.request.contextPath}/students" class="btn btn-secondary">Cancel</a>
                    </c:if>
                </div>
            </form>
        </div>
    </c:if>

    <!-- Student List -->
    <div class="card">
        <h3>
            Student List
            <c:if test="${loggedUser.role == 1}">
                <small>— Top 5 Highest GPA</small>
            </c:if>
        </h3>

        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Student ID</th>
                    <th>Name</th>
                    <th>GPA</th>
                    <th>Department</th>
                    <th>Created By</th>
                    <th>Created At</th>
                    <th>Updated At</th>
                    <c:if test="${loggedUser.role == 2}">
                        <th>Actions</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty students}">
                        <tr><td colspan="9" style="text-align:center;color:#888;">No students found.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="s" items="${students}" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td>${s.studentId}</td>
                                <td>${s.name}</td>
                                <td>
                                    <span class="gpa-badge gpa-${s.gpa >= 8 ? 'high' : s.gpa >= 5 ? 'mid' : 'low'}">
                                        <fmt:formatNumber value="${s.gpa}" maxFractionDigits="1"/>
                                    </span>
                                </td>
                                <td>${s.department.departmentName}</td>
                                <td>${s.createdBy}</td>
                                <td>${s.createdAt}</td>
                                <td>${s.updatedAt}</td>
                                <c:if test="${loggedUser.role == 2}">
                                    <td>
                                        <c:if test="${s.createdBy == loggedUser.username}">
                                            <a href="${pageContext.request.contextPath}/students?action=edit&id=${s.id}"
                                               class="btn btn-sm btn-secondary">✏️ Edit</a>
                                            <a href="${pageContext.request.contextPath}/students?action=delete&id=${s.id}"
                                               class="btn btn-sm btn-danger"
                                               onclick="return confirm('Delete this student?')">🗑️ Delete</a>
                                        </c:if>
                                        <c:if test="${s.createdBy != loggedUser.username}">
                                            <span class="text-muted">—</span>
                                        </c:if>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>

        <!-- Pagination (Staff only) -->
        <c:if test="${loggedUser.role == 2 && totalPages > 1}">
            <div class="pagination">
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <a href="${pageContext.request.contextPath}/students?page=${p}"
                       class="page-link ${p == currentPage ? 'active' : ''}">${p}</a>
                </c:forEach>
            </div>
        </c:if>
    </div>

</div>
</body>
</html>
