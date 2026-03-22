<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quyền Quản Trị - QLGNBT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        body { background-color: #f8f9fa; }
        .table-responsive { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    </style>
</head>
<body>
<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="#">QLGNBT Admin</a>
        <div class="d-flex">
            <span class="navbar-text me-3 text-white">Xin chào, Admin!</span>
            <a href="logout" class="btn btn-outline-danger btn-sm">Đăng xuất</a>
        </div>
    </div>
</nav>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0"><i class="fas fa-users-cog"></i> Quản Lý Tài Khoản</h2>
        <a href="assignments" class="btn btn-info shadow"><i class="fas fa-tasks"></i> Danh Sách Bài Tập (Quản Trị)</a>
    </div>
    
    <c:if test="${not empty errorMsg}"><div class="alert alert-danger">${errorMsg}</div></c:if>
    <c:if test="${not empty successMsg}"><div class="alert alert-success">${successMsg}</div></c:if>
    
    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Tài Khỏan</th>
                    <th>Họ Tên</th>
                    <th>Vai Trò</th>
                    <th>Lớp (ID)</th>
                    <th>Trạng Thái</th>
                    <th>Thao Tác</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td>${u.userId}</td>
                        <td>${u.username}</td>
                        <td>${u.fullName}</td>
                        <td>
                            <span class="badge bg-${u.role == 'admin' ? 'danger' : (u.role == 'teacher' ? 'primary' : 'secondary')}">${u.role}</span>
                        </td>
                        <td>${u.classId != null ? u.classId : '-'}</td>
                        <td>
                            <span class="badge bg-${u.status == 'ACTIVE' ? 'success' : (u.status == 'PENDING' ? 'warning' : 'danger')}">${u.status}</span>
                        </td>
                        <td>
                            <div class="btn-group btn-group-sm">
                                <c:if test="${u.status == 'PENDING' && u.role != 'admin'}">
                                    <form action="admin" method="post" class="d-inline">
                                        <input type="hidden" name="action" value="approve">
                                        <input type="hidden" name="targetUserId" value="${u.userId}">
                                        <button type="submit" class="btn btn-success me-1" title="Duyệt"><i class="fas fa-check"></i></button>
                                    </form>
                                    <form action="admin" method="post" class="d-inline">
                                        <input type="hidden" name="action" value="reject">
                                        <input type="hidden" name="targetUserId" value="${u.userId}">
                                        <button type="submit" class="btn btn-warning me-1" title="Từ chối"><i class="fas fa-times"></i></button>
                                    </form>
                                </c:if>

                                <c:if test="${u.role != 'admin'}">
                                    <!-- Edit Password Btn -->
                                    <button class="btn btn-info text-white me-1" data-bs-toggle="modal" data-bs-target="#editPasswordModal${u.userId}" title="Đổi Mật Khẩu"><i class="fas fa-key"></i></button>
                                    <!-- Delete Btn -->
                                    <form action="admin" method="post" class="d-inline" onsubmit="return confirm('Bạn có chắc chắn muốn xóa tài khoản này?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="targetUserId" value="${u.userId}">
                                        <button type="submit" class="btn btn-danger" title="Xóa"><i class="fas fa-trash"></i></button>
                                    </form>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- Modals (Placed outside table to prevent browser DOM nesting bugs) -->
    <c:forEach var="u" items="${users}">
        <c:if test="${u.role != 'admin'}">
            <!-- Edit Password Modal -->
            <div class="modal fade" id="editPasswordModal${u.userId}" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog">
                    <form action="admin" method="post">
                        <div class="modal-content">
                            <div class="modal-header bg-dark text-white">
                                <h5 class="modal-title">Đổi mật khẩu cho: ${u.username}</h5>
                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="action" value="reset_password">
                                <input type="hidden" name="targetUserId" value="${u.userId}">
                                <div class="mb-3">
                                    <label class="form-label">Mật khẩu mới</label>
                                    <input type="password" name="newPassword" class="form-control" required minlength="3">
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
    </c:forEach>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
