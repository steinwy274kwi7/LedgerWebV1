<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>알림</title>
</head>
<body>
    <script>
        // 1. Controller에서 model.addAttribute("msg", "..."); 로 보낸 메시지가 있다면 띄움
        <c:if test="${not empty msg}">
            alert('${msg}');
        </c:if>

        // 2. Controller에서 model.addAttribute("url", "/main.do"); 로 보낸 경로가 있다면 이동
        <c:choose>
            <c:when test="${not empty url}">
                location.href = '${pageContext.request.contextPath}${url}';
            </c:when>
            <c:otherwise>
                // url이 지정되지 않았다면 기본적으로 이전 페이지로 백업
                history.back();
            </c:otherwise>
        </c:choose>
    </script>
</body>
</html>