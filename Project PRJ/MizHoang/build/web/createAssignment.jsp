<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Giao bài tập mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
    <div class="card">
        <div class="card-header bg-success text-white">
            <h4>Giao bài tập mới</h4>
        </div>
        <div class="card-body">
            <form action="createAssignment" method="post">
                <div class="mb-3">
                    <label>Tiêu đề</label>
                    <input type="text" name="title" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label>Mô tả</label>
                    <textarea name="description" class="form-control" rows="5"></textarea>
                </div>
                <div class="mb-3">
                    <label>Hạn nộp</label>
                    <input type="date" name="due_date" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-success">Giao bài</button>
                <a href="assignments" class="btn btn-secondary">Quay lại</a>
            </form>
        </div>
    </div>
</div>
</body>
</html>