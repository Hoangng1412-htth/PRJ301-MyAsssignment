/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.ArrayList;
import model.RequestForLeave;
import java.sql.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Division;
import model.Employee;
import model.iam.User;

/**
 *
 * @author sonnt
 */
public class RequestForLeaveDBContext extends DBContext<RequestForLeave> {

    public ArrayList<RequestForLeave> getByEmployeeAndSubodiaries(int eid) {
        ArrayList<RequestForLeave> rfls = new ArrayList<>();
        try {
            String sql = """
                                    WITH Org AS (
                                     	SELECT *, 0 AS lvl FROM Employee e WHERE e.eid = ?
                                     	UNION ALL
                                     	SELECT c.*, o.lvl + 1 AS lvl FROM Employee c JOIN Org o ON c.supervisorid = o.eid
                                     )
                                     SELECT
                                     	r.[rid],
                                     	r.[created_by],
                                     	e.ename AS [created_name],
                                     	d.dname AS [division_name],
                                     	r.[created_time],
                                     	r.[from],
                                     	r.[to],
                                     	r.[reason],
                                     	r.[status],
                                     	r.[processed_by],
                                     	r.[type],
                                     	p.ename AS [processed_name]
                                     FROM Org e
                                     INNER JOIN [RequestForLeave] r ON e.eid = r.created_by
                                     LEFT JOIN Employee p ON p.eid = r.processed_by
                                     LEFT JOIN Division d ON e.did = d.did;""";
             PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, eid);
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            RequestForLeave rfl = new RequestForLeave();

            // Gán dữ liệu cơ bản
            rfl.setId(rs.getInt("rid"));
            rfl.setCreated_time(rs.getTimestamp("created_time"));
            rfl.setFrom(rs.getDate("from"));
            rfl.setTo(rs.getDate("to"));
            rfl.setReason(rs.getString("reason"));
            rfl.setStatus(rs.getInt("status"));
            rfl.setType(rs.getString("type"));

            // Gán người tạo đơn
            Employee created_by = new Employee();
            created_by.setId(rs.getInt("created_by"));
            created_by.setName(rs.getString("created_name"));

            // Gán phòng ban
            Division div = new Division();
            div.setDname(rs.getString("division_name"));
            created_by.setDiv(div);

            rfl.setCreated_by(created_by);

            // Gán người xử lý nếu có
            int processed_by_id = rs.getInt("processed_by");
            if (processed_by_id != 0) {
                Employee processed_by = new Employee();
                processed_by.setId(processed_by_id);
                processed_by.setName(rs.getString("processed_name"));
                rfl.setProcessed_by(processed_by);
            }

            rfls.add(rfl);
        }
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        closeConnection();
    }
    return rfls;
    }

    @Override
    public ArrayList<RequestForLeave> list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public RequestForLeave get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

@Override
public void insert(RequestForLeave model) {
    try {
        String sql = "INSERT INTO RequestForLeave "
                   + "(created_by, created_time, [from], [to], reason,[type], status) "
                   + "VALUES (?, GETDATE(), ?, ?, ?,?, 0)";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, model.getCreated_by().getId());
        stm.setDate(2, model.getFrom());
        stm.setDate(3, model.getTo());
        stm.setString(4, model.getReason());
        stm.setString(5, model.getType());
        stm.executeUpdate();
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    } 
//    finally {
//        closeConnection();
//    }
}

    @Override
    public void update(RequestForLeave model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

   @Override
public void delete(RequestForLeave model) {
    try {
        String sql = "DELETE FROM RequestForLeave WHERE rid = ?";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, model.getId());
        stm.executeUpdate();
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    } 
//    finally {
//        closeConnection();
//    }
}

public ArrayList<RequestForLeave> getRequestsByRole(User user) {
    ArrayList<RequestForLeave> list = new ArrayList<>();
    try {
        String sql = """
            WITH Org AS (
                SELECT e.eid, e.did, 0 AS lvl
                FROM Employee e
                WHERE e.eid = ?

                UNION ALL
                SELECT c.eid, c.did, o.lvl + 1
                FROM Employee c
                JOIN Org o ON c.supervisorid = o.eid
            )
            SELECT 
                r.rid,
                e.ename AS employee_name,
                d.dname AS division_name,
                r.[from],
                r.[to],
                DATEDIFF(day, r.[from], r.[to]) + 1 AS total_days,
                r.reason,
                r.status,
                p.ename AS processed_name
            FROM RequestForLeave r
            INNER JOIN Employee e ON e.eid = r.created_by
            LEFT JOIN Employee p ON p.eid = r.processed_by
            LEFT JOIN Division d ON e.did = d.did
            WHERE 1=1
        """;

        // Nếu user là Director → xem tất cả
        boolean isDirector = user.getRoles().stream()
            .anyMatch(r -> r.getName().equalsIgnoreCase("Director"));
        if (!isDirector) {
            sql += " AND (r.created_by IN (SELECT eid FROM Org))";
        }

        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, user.getEmployee().getId());
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            RequestForLeave r = new RequestForLeave();
            r.setId(rs.getInt("rid"));
            r.setFrom(rs.getDate("from"));
            r.setTo(rs.getDate("to"));
            r.setReason(rs.getString("reason"));
            r.setStatus(rs.getInt("status"));

            Employee createdBy = new Employee();
            createdBy.setName(rs.getString("employee_name"));
            Division div = new Division();
            div.setDname(rs.getString("division_name"));
            createdBy.setDiv(div);
            r.setCreated_by(createdBy);

            if (rs.getString("processed_name") != null) {
                Employee processedBy = new Employee();
                processedBy.setName(rs.getString("processed_name"));
                r.setProcessed_by(processedBy);
            }

            list.add(r);
        }
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        closeConnection();
    }
    return list;
}
public void updateRequest(RequestForLeave r) {
    try {
        String sql = """
            UPDATE RequestForLeave
            SET [from] = ?, 
                [to] = ?, 
                reason = ?, 
                type = ?, 
                status = ?, 
                processed_by = ?
            WHERE rid = ?
        """;
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setDate(1, r.getFrom());
        stm.setDate(2, r.getTo());
        stm.setString(3, r.getReason());
        stm.setString(4, r.getType()); // ✅ thêm type
        stm.setInt(5, r.getStatus());

        // Nếu có người duyệt thì set processed_by, nếu không thì null
        if (r.getProcessed_by() != null) {
            stm.setInt(6, r.getProcessed_by().getId());
        } else {
            stm.setNull(6, java.sql.Types.INTEGER);
        }

        stm.setInt(7, r.getId());
        stm.executeUpdate();
         connection.commit();

    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName())
              .log(Level.SEVERE, null, ex);
    } 
}



public RequestForLeave getById(int id) {
    try {
        String sql = """
            SELECT 
                r.rid,
                r.[from],
                r.[to],
                r.reason,
                r.status,
                r.type,
                r.created_by AS created_by_id,
                e.ename AS created_name,
                e.did AS created_did,
                d.dname AS division_name,
                r.processed_by AS processed_by_id,
                p.ename AS processed_name
            FROM RequestForLeave r
            INNER JOIN Employee e ON e.eid = r.created_by
            LEFT JOIN Division d ON e.did = d.did
            LEFT JOIN Employee p ON p.eid = r.processed_by
            WHERE r.rid = ?
        """;

        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, id);
        ResultSet rs = stm.executeQuery();

        if (rs.next()) {
            RequestForLeave r = new RequestForLeave();
            r.setId(rs.getInt("rid"));
            r.setFrom(rs.getDate("from"));
            r.setTo(rs.getDate("to"));
            r.setReason(rs.getString("reason"));
            r.setStatus(rs.getInt("status"));
            r.setType(rs.getString("type"));

            // 🧩 Gán thông tin người tạo
            Employee createdBy = new Employee();
            createdBy.setId(rs.getInt("created_by_id"));
            createdBy.setName(rs.getString("created_name"));

            Division div = new Division();
            div.setDname(rs.getString("division_name"));
            div.setId(rs.getInt("created_did"));
            createdBy.setDiv(div);
            r.setCreated_by(createdBy);

            // 🧩 Gán thông tin người duyệt (nếu có)
            int processedById = rs.getInt("processed_by_id");
            if (!rs.wasNull()) {
                Employee processedBy = new Employee();
                processedBy.setId(processedById);
                processedBy.setName(rs.getString("processed_name"));
                r.setProcessed_by(processedBy);
            }

            return r;
        }
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
   } 
    //finally {
//        closeConnection();
//    }
    return null;
}

public ArrayList<Employee> getEmployeeAndSubordinates(int eid) {
    ArrayList<Employee> list = new ArrayList<>();
    try {
        String sql = """
            WITH Org AS (
                SELECT e.eid, e.ename, e.did, e.supervisorid
                FROM Employee e WHERE e.eid = ?
                UNION ALL
                SELECT c.eid, c.ename, c.did, c.supervisorid
                FROM Employee c
                INNER JOIN Org o ON c.supervisorid = o.eid
            )
            SELECT o.eid, o.ename, d.dname
            FROM Org o
            LEFT JOIN Division d ON o.did = d.did
        """;

        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, eid);
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            Employee e = new Employee();
            e.setId(rs.getInt("eid"));
            e.setName(rs.getString("ename"));

            Division d = new Division();
            d.setDname(rs.getString("dname"));
            e.setDiv(d);

            list.add(e);
        }
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    }
    return list;
}
public ArrayList<Employee> getEmployeesByDivision(String divisionId) {
    ArrayList<Employee> list = new ArrayList<>();
    try {
        String sql = """
            SELECT e.eid, e.ename, d.dname
            FROM Employee e
            LEFT JOIN Division d ON e.did = d.did
        """;

        if (divisionId != null && !divisionId.isEmpty()) {
            sql += " WHERE e.did = ?";
        }

        PreparedStatement stm = connection.prepareStatement(sql);
        if (divisionId != null && !divisionId.isEmpty()) {
            stm.setString(1, divisionId);
        }

        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            Employee e = new Employee();
            e.setId(rs.getInt("eid"));
            e.setName(rs.getString("ename"));

            Division d = new Division();
            d.setDname(rs.getString("dname"));
            e.setDiv(d);

            list.add(e);
        }
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    }
    return list;
}
public HashMap<Integer, ArrayList<RequestForLeave>> getLeavesInRange(
        ArrayList<Employee> employees, Date from, Date to) {

    HashMap<Integer, ArrayList<RequestForLeave>> map = new HashMap<>();
    if (employees == null || employees.isEmpty()) return map;

    try {
        // 1️⃣ Tạo chuỗi placeholder: ?, ?, ?, ...
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < employees.size(); i++) {
            placeholders.append("?");
            if (i < employees.size() - 1) placeholders.append(",");
        }

        // 2️⃣ SQL chỉ lấy đơn đã duyệt, có giao với tháng cần xem
  StringBuilder sql = new StringBuilder();
sql.append("SELECT r.rid, r.created_by, r.[from], r.[to], r.reason, r.status ")
   .append("FROM RequestForLeave r ")
   .append("WHERE r.status = 1 ")
   .append("AND r.created_by IN (")
   .append(placeholders)
   .append(") ")
   .append("AND r.[from] <= ? ")
   .append("AND r.[to] >= ?");
PreparedStatement stm = connection.prepareStatement(sql.toString());



        // 3️⃣ Set danh sách employeeId
        int idx = 1;
        for (Employee e : employees) {
            stm.setInt(idx++, e.getId());
        }

        // 4️⃣ Set tham số ngày
        stm.setDate(idx++, to);
        stm.setDate(idx, from);

        // 5️⃣ Đọc kết quả
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            int empId = rs.getInt("created_by");

            RequestForLeave r = new RequestForLeave();
            r.setId(rs.getInt("rid"));
            r.setFrom(rs.getDate("from"));
            r.setTo(rs.getDate("to"));
            r.setReason(rs.getString("reason"));
            r.setStatus(rs.getInt("status"));

            map.computeIfAbsent(empId, k -> new ArrayList<>()).add(r);
        }
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    }

    return map;
}


public ArrayList<String> getAllDivisions() {
    ArrayList<String> divisions = new ArrayList<>();
    String sql = "SELECT dname FROM Division ORDER BY dname";

    try (PreparedStatement stm = connection.prepareStatement(sql);
         ResultSet rs = stm.executeQuery()) {

        while (rs.next()) {
            divisions.add(rs.getString("dname"));
        }
        System.out.println("✅ Found " + divisions.size() + " divisions: " + divisions);

    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    }

    return divisions;
}



public ArrayList<RequestForLeave> getRequestsByFilter(
        User user, String searchName, String fromDate, String toDate,
        String status, String division) {

    ArrayList<RequestForLeave> list = new ArrayList<>();

    try {
        StringBuilder sql = new StringBuilder("""
            WITH Org AS (
                SELECT e.eid, e.did
                FROM Employee e WHERE e.eid = ?
                UNION ALL
                SELECT c.eid, c.did
                FROM Employee c
                JOIN Org o ON c.supervisorid = o.eid
            )
            SELECT 
                r.rid, r.created_by, r.[from], r.[to], r.reason, r.status, r.type,
                e.ename AS employee_name, d.dname AS division_name,
                p.eid AS processed_id, p.ename AS processed_name
            FROM RequestForLeave r
            INNER JOIN Employee e ON e.eid = r.created_by
            LEFT JOIN Division d ON e.did = d.did
            LEFT JOIN Employee p ON p.eid = r.processed_by
            WHERE 1=1
        """);

        // ✅ Quyền: Director xem tất cả, các vai trò khác chỉ xem của mình & cấp dưới
        boolean isDirector = user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("Director"));
        if (!isDirector) {
            sql.append(" AND r.created_by IN (SELECT eid FROM Org)");
        }

        // ✅ Bộ lọc
        if (!searchName.isEmpty())
            sql.append(" AND e.ename LIKE ?");
        if (!fromDate.isEmpty())
            sql.append(" AND r.[from] >= ?");
        if (!toDate.isEmpty())
            sql.append(" AND r.[to] <= ?");
        if (!status.isEmpty())
            sql.append(" AND r.status = ?");
        if (isDirector && !division.isEmpty())
            sql.append(" AND d.dname LIKE ?");

        sql.append(" ORDER BY r.created_time DESC");

        PreparedStatement stm = connection.prepareStatement(sql.toString());

        int i = 1;
        stm.setInt(i++, user.getEmployee().getId());
        if (!searchName.isEmpty()) stm.setString(i++, "%" + searchName + "%");
        if (!fromDate.isEmpty()) stm.setDate(i++, Date.valueOf(fromDate));
        if (!toDate.isEmpty()) stm.setDate(i++, Date.valueOf(toDate));
        if (!status.isEmpty()) stm.setInt(i++, Integer.parseInt(status));
        if (isDirector && !division.isEmpty()) stm.setString(i++, "%" + division + "%");

        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            RequestForLeave r = new RequestForLeave();
            r.setId(rs.getInt("rid"));
            r.setFrom(rs.getDate("from"));
            r.setTo(rs.getDate("to"));
            r.setReason(rs.getString("reason"));
            r.setStatus(rs.getInt("status"));
            r.setType(rs.getString("type"));

            Employee created = new Employee();
            created.setId(rs.getInt("created_by"));
            created.setName(rs.getString("employee_name"));
            Division div = new Division();
            div.setDname(rs.getString("division_name"));
            created.setDiv(div);
            r.setCreated_by(created);

            int processedId = rs.getInt("processed_id");
            if (!rs.wasNull()) {
                Employee processed = new Employee();
                processed.setId(processedId);
                processed.setName(rs.getString("processed_name"));
                r.setProcessed_by(processed);
            }

            list.add(r);
        }

        System.out.println("✅ Filter result: " + list.size() + " records found");

    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName())
              .log(Level.SEVERE, null, ex);
    } finally {
        closeConnection();
    }
    return list;
}

public ArrayList<RequestForLeave> getFilteredRequests(
        User user, String name, String fromDate, String toDate,
        String status, String division) {

    ArrayList<RequestForLeave> list = new ArrayList<>();
    try {
        StringBuilder sql = new StringBuilder("""
            WITH Org AS (
                SELECT e.eid, e.did FROM Employee e WHERE e.eid = ?
                UNION ALL
                SELECT c.eid, c.did FROM Employee c
                JOIN Org o ON c.supervisorid = o.eid
            )
            SELECT 
                r.rid, r.[from], r.[to], r.reason, r.status, r.type,
                e.eid AS created_by, e.ename AS employee_name, d.dname AS division_name,
                p.eid AS processed_id, p.ename AS processed_name
            FROM RequestForLeave r
            INNER JOIN Employee e ON e.eid = r.created_by
            LEFT JOIN Employee p ON p.eid = r.processed_by
            LEFT JOIN Division d ON e.did = d.did
            WHERE 1=1
        """);

        // 🔹 Giới hạn quyền truy cập (chỉ Director thấy tất cả)
        boolean isDirector = user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("Director"));
        if (!isDirector) {
            sql.append(" AND r.created_by IN (SELECT eid FROM Org)");
        }

        // 🔹 Thêm điều kiện lọc
        if (!name.isEmpty()) sql.append(" AND e.ename LIKE ?");
        if (!fromDate.isEmpty()) sql.append(" AND r.[from] >= ?");
        if (!toDate.isEmpty()) sql.append(" AND r.[to] <= ?");
        if (!status.isEmpty()) sql.append(" AND r.status = ?");
        if (!division.isEmpty()) sql.append(" AND d.dname LIKE ?");

        PreparedStatement stm = connection.prepareStatement(sql.toString());
        int i = 1;
        stm.setInt(i++, user.getEmployee().getId());
        if (!name.isEmpty()) stm.setString(i++, "%" + name + "%");
        if (!fromDate.isEmpty()) stm.setDate(i++, Date.valueOf(fromDate));
        if (!toDate.isEmpty()) stm.setDate(i++, Date.valueOf(toDate));
        if (!status.isEmpty()) stm.setInt(i++, Integer.parseInt(status));
        if (!division.isEmpty()) stm.setString(i++, "%" + division + "%");

        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            RequestForLeave r = new RequestForLeave();
            r.setId(rs.getInt("rid"));
            r.setFrom(rs.getDate("from"));
            r.setTo(rs.getDate("to"));
            r.setReason(rs.getString("reason"));
            r.setStatus(rs.getInt("status"));
            r.setType(rs.getString("type"));

            Employee created = new Employee();
            created.setId(rs.getInt("created_by"));
            created.setName(rs.getString("employee_name"));
            Division div = new Division();
            div.setDname(rs.getString("division_name"));
            created.setDiv(div);
            r.setCreated_by(created);

            int processedId = rs.getInt("processed_id");
            if (!rs.wasNull()) {
                Employee processed = new Employee();
                processed.setId(processedId);
                processed.setName(rs.getString("processed_name"));
                r.setProcessed_by(processed);
            }

            list.add(r);
        }
        System.out.println("✅ Filter result: " + list.size() + " records found");

    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    }
    return list;
}
public ArrayList<Division> getAllDivisionsForAgenda() {
    ArrayList<Division> divisions = new ArrayList<>();
    String sql = "SELECT did, dname FROM Division ORDER BY dname";

    try (PreparedStatement stm = connection.prepareStatement(sql);
         ResultSet rs = stm.executeQuery()) {

        while (rs.next()) {
            Division d = new Division();
            d.setId(rs.getInt("did"));       // dùng int vì Division kế thừa BaseModel
            d.setDname(rs.getString("dname"));
            divisions.add(d);
        }

        System.out.println("✅ [Agenda] Found " + divisions.size() + " divisions");

    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName())
              .log(Level.SEVERE, null, ex);
    }

    return divisions;
}

}
