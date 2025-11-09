<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch làm việc</title>
    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            margin: 20px;
            background-color: #f6f6f6;
        }

        .calendar-container {
            max-width: 1200px;
            margin: 0 auto;
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 20px;
        }

        .calendar-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 10px;
            margin-bottom: 20px;
        }

        .calendar-header h2 {
            font-size: 22px;
            color: #2c3e50;
        }

        .calendar-header a {
            text-decoration: none;
            color: #333;
            background: #eee;
            padding: 6px 12px;
            border-radius: 6px;
            font-weight: bold;
            transition: 0.2s;
        }

        .calendar-header a:hover {
            background-color: #ddd;
        }

        .division-select {
            background: #f8f8f8;
            border: 1px solid #ccc;
            padding: 6px 10px;
            border-radius: 6px;
            font-size: 14px;
        }

        .calendar-grid {
            display: grid;
            grid-template-columns: repeat(7, 1fr);
            border: 1px solid #ddd;
            border-radius: 10px;
            overflow: hidden;
        }

        .calendar-weekday {
            background-color: #f8f8f8;
            text-align: center;
            font-weight: bold;
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }

        .calendar-day {
            border: 1px solid #eee;
            min-height: 110px;
            padding: 6px;
            font-size: 13px;
            position: relative;
            vertical-align: top;
        }

        .day-number {
            font-weight: bold;
            color: #444;
            font-size: 14px;
        }

        .leave-entry {
            margin-top: 3px;
            border-radius: 4px;
            padding: 3px 5px;
            font-size: 11px;
            color: #fff;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .leave-entry.approved { background: #e74c3c; }
        .leave-entry.pending { background: #f39c12; }
        .leave-entry.rejected { background: #e74c3c; }

        .no-data {
            text-align: center;
            color: #ccc;
            font-size: 11px;
            margin-top: 15px;
        }

        @media (max-width: 800px) {
            .calendar-grid {
                font-size: 12px;
            }
        }
    </style>
</head>
<body>

<div class="calendar-container">

    <!-- ==== HEADER ==== -->
    <div class="calendar-header">
        <div style="display:flex; align-items:center; gap:10px;">
            <a href="?month=${month-1}&year=${year}<c:if test='${isDirector}'>&division=${selectedDivision}</c:if>">◀ Trước</a>
            <h2>Tháng ${month}/${year}</h2>
            <a href="?month=${month+1}&year=${year}<c:if test='${isDirector}'>&division=${selectedDivision}</c:if>">Tiếp ▶</a>
        </div>

        <!-- Dropdown chỉ cho Director -->
        <c:if test="${isDirector}">
            <form method="get" action="${pageContext.request.contextPath}/request/agenda">
                <input type="hidden" name="month" value="${month}">
                <input type="hidden" name="year" value="${year}">
                <select name="division" class="division-select" onchange="this.form.submit()">
                    <option value="">Toàn công ty</option>
                    <c:forEach var="d" items="${divisions}">
                        <option value="${d.id}" ${d.id == selectedDivision ? 'selected' : ''}>
                            ${d.dname}
                        </option>
                    </c:forEach>
                </select>
            </form>
        </c:if>
    </div>

    <!-- ==== HEADER CÁC NGÀY ==== -->
    <div class="calendar-grid">
        <div class="calendar-weekday">CN</div>
        <div class="calendar-weekday">T2</div>
        <div class="calendar-weekday">T3</div>
        <div class="calendar-weekday">T4</div>
        <div class="calendar-weekday">T5</div>
        <div class="calendar-weekday">T6</div>
        <div class="calendar-weekday">T7</div>

        <%
            java.time.LocalDate first = java.time.LocalDate.of(
                (Integer)request.getAttribute("year"),
                (Integer)request.getAttribute("month"),
                1
            );
            int startDay = first.getDayOfWeek().getValue() % 7; // CN = 0
            int days = (Integer)request.getAttribute("daysInMonth");
            int totalCells = ((startDay + days) <= 35) ? 35 : 42;
            pageContext.setAttribute("startDay", startDay);
            pageContext.setAttribute("days", days);
            pageContext.setAttribute("totalCells", totalCells);
        %>

        <!-- ==== LƯỚI NGÀY ==== -->
        <c:forEach var="i" begin="1" end="${totalCells}">
            <%
                int index = (Integer)pageContext.getAttribute("i");
                int sd = (Integer)pageContext.getAttribute("startDay");
                int dayNum = index - sd;
                boolean isDay = dayNum >= 1 && dayNum <= (Integer)pageContext.getAttribute("days");
                pageContext.setAttribute("dayNum", isDay ? dayNum : 0);
            %>

            <div class="calendar-day">
                <c:if test="${dayNum > 0}">
                    <div class="day-number">${dayNum}</div>

                    <c:set var="hasData" value="false"/>
                    <c:forEach var="emp" items="${employees}">
                        <c:forEach var="req" items="${leaves[emp.id]}">
                            <c:if test="${dayNum >= req.from.date && dayNum <= req.to.date}">
                                <div class="leave-entry approved">
                                    ${emp.name}
                                </div>
                                <c:set var="hasData" value="true"/>
                            </c:if>
                        </c:forEach>
                    </c:forEach>

                    <c:if test="${!hasData}">
                        <div class="no-data"></div>
                    </c:if>
                </c:if>
            </div>
        </c:forEach>
    </div>
</div>

</body>
</html>
