<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Danh sách bài tập - MizHoang</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>
    <body class="bg-light">
        <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
            <div class="container">
                <a class="navbar-brand" href="#">MizHoang</a>
                <div class="navbar-text text-white">
                    Xin chào, <strong>${user.fullName}</strong> (${user.role})
                </div>
                <a href="logout" class="btn btn-outline-light">Đăng xuất</a>
            </div>
        </nav>

        <div class="container mt-4">
            <div class="d-flex justify-content-between mb-3">
                <form class="d-flex" method="get" action="assignments">
                    <input type="text" name="search" class="form-control me-2" placeholder="Tìm kiếm bài tập..." value="${param.search}">
                    <button class="btn btn-primary"><i class="fas fa-search"></i></button>
                </form>

                <c:if test="${user.role == 'teacher'}">
                    <a href="createAssignment.jsp" class="btn btn-success">
                        <i class="fas fa-plus"></i> Giao bài mới
                    </a>
                </c:if>
            </div>

            <table class="table table-hover align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>Tiêu đề</th>
                        <th>Mô tả</th>
                        <th>Hạn nộp</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="a" items="${list}">
                        <tr>
                            <td><strong>${a.title}</strong></td>
                            <td>${a.description}</td>
                            <td><fmt:formatDate value="${a.dueDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${a.status == 'Đã nộp'}">
                                        <span class="badge bg-success">Đã xong</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger">Chưa làm</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                            <td>
                                <!-- STUDENT -->
                                <c:if test="${user.role == 'student'}">

                                    <c:if test="${a.status != 'Đã nộp'}">
                                        <form action="submit" method="post" enctype="multipart/form-data" style="display:inline;">
                                            <input type="hidden" name="assignmentId" value="${a.assignmentId}">
                                            <input type="file" name="file" required>
                                            <button class="btn btn-primary btn-sm">Nộp bài</button>
                                        </form>
                                    </c:if>

                                    <c:if test="${a.status == 'Đã nộp'}">
                                        <form action="submit" method="post" enctype="multipart/form-data" style="display:inline;">
                                            <input type="hidden" name="assignmentId" value="${a.assignmentId}">
                                            <input type="file" name="file" required>
                                            <button class="btn btn-warning btn-sm">Nộp lại</button>
                                        </form>
                                    </c:if>

                                </c:if>

                                <!-- TEACHER -->
                                <c:if test="${user.role == 'teacher'}">
                                    <a href="delete-assignment?id=${a.assignmentId}" 
                                       onclick="return confirm('Bạn có chắc muốn xóa bài này không?')"
                                       class="btn btn-danger btn-sm">
                                        Xóa
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </body>
</html>