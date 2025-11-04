<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${pageTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <!-- ✅ include đúng vị trí, vì header.jsp và sidebar.jsp nằm cùng thư mục -->
    <jsp:include page="header.jsp" />
    <jsp:include page="sidebar.jsp" />

    <div class="content">
        <jsp:include page="${contentPage}" />
    </div>
</body>
</html>
