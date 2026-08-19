<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>새 공동 가계부 만들기</title>
    <style>
        .form-container { width: 500px; margin: 50px auto; background: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); font-family: sans-serif; }
        .form-container h2 { text-align: center; color: #333; margin-bottom: 30px; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; font-weight: bold; margin-bottom: 8px; color: #555; }
        .form-group input[type="text"], .form-group textarea { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box; }
        .radio-card { display: flex; gap: 15px; }
        .radio-card label { flex: 1; border: 1px solid #ddd; padding: 15px; border-radius: 8px; cursor: pointer; text-align: center; transition: all 0.2s; }
        .radio-card input[type="radio"] { display: none; }
        .radio-card input[type="radio"]:checked + div { color: #007BFF; font-weight: bold; }
        .radio-card label:has(input[type="radio"]:checked) { border-color: #007BFF; background: #f0f8ff; }
        .btn-submit { width: 100%; background: #28a745; color: white; border: none; padding: 12px; font-size: 1.1em; font-weight: bold; border-radius: 5px; cursor: pointer; margin-top: 10px; }
        .btn-cancel { width: 100%; background: #6c757d; color: white; border: none; padding: 12px; font-size: 1.1em; border-radius: 5px; cursor: pointer; margin-top: 10px; text-decoration: none; display: block; text-align: center; box-sizing: border-box; }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>✨ 새 공동 가계부 만들기</h2>
        
        <c:if test="${not empty msg}">
            <div style="color: red; text-align: center; margin-bottom: 15px; font-weight: bold;">${msg}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/group/create.do" method="post" onsubmit="return validateForm()">
            
            <div class="form-group">
                <label>방 이름 (최대 20자)</label>
                <input type="text" name="groupName" id="groupName" maxlength="20" placeholder="예: ✈️ 제주도 여행계, 🏠 자취방" required>
            </div>

            <div class="form-group">
                <label>간단한 설명 (선택)</label>
                <textarea name="groupDesc" rows="3" maxlength="150" placeholder="어떤 목적으로 사용하는 방인지 적어주세요."></textarea>
            </div>

            <div class="form-group">
                <label>정산 방식 선택</label>
                <div class="radio-card">
                    <label>
                        <input type="radio" name="groupType" value="M" checked>
                        <div>📅 매월 정산형<br><span style="font-size:0.8em; color:#888; font-weight:normal;">월마다 모임비/회비를 정산해요</span></div>
                    </label>
                    <label>
                        <input type="radio" name="groupType" value="I">
                        <div>💸 자유 정산형<br><span style="font-size:0.8em; color:#888; font-weight:normal;">건별로 자유롭게 쓰고 1/N 해요</span></div>
                    </label>
                </div>
            </div>

            <div class="form-group">
                <label>공개 여부 (검색 허용)</label>
                <select name="groupOpenYn" style="width: 100%; padding: 10px; border-radius: 5px; border: 1px solid #ccc;">
                    <option value="N">비공개 (초대로만 참여 가능)</option>
                    <option value="Y">공개 (아이디 검색으로 방 노출)</option>
                </select>
            </div>

            <button type="submit" class="btn-submit">만들기</button>
            <a href="${pageContext.request.contextPath}/group/list.do" class="btn-cancel">취소</a>
        </form>
    </div>

    <script>
        function validateForm() {
            const name = document.getElementById('groupName').value.trim();
            if (!name) {
                alert("방 이름을 입력해 주세요.");
                return false;
            }
            if (name.length > 20) {
                alert("방 이름은 20자를 초과할 수 없습니다.");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>