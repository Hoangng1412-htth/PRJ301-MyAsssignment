/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.ArrayList;
import model.RequestForLeave;
import java.sql.*;
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
public ArrayList<RequestForLeave> getRequestsByRolePaged(User user, int pageIndex, int pageSize) {
    ArrayList<RequestForLeave> list = new ArrayList<>();
    try {
        String sql = """
            WITH Org AS (
                SELECT e.eid, e.did, 0 AS lvl
                FROM Employee e WHERE e.eid = ?
                UNION ALL
                SELECT c.eid, c.did, o.lvl + 1
                FROM Employee c
                JOIN Org o ON c.supervisorid = o.eid
            )
            SELECT * FROM (
                SELECT 
                    ROW_NUMBER() OVER (ORDER BY r.created_time DESC) AS row_index,
                    r.rid, e.ename AS employee_name, d.dname AS division_name,
                    r.[from], r.[to],r.type,
                    DATEDIFF(day, r.[from], r.[to]) + 1 AS total_days,
                    r.reason, r.status, p.ename AS processed_name
                FROM RequestForLeave r
                INNER JOIN Employee e ON e.eid = r.created_by
                LEFT JOIN Employee p ON p.eid = r.processed_by
                LEFT JOIN Division d ON e.did = d.did
                WHERE 1=1
        """;

        boolean isDirector = user.getRoles().stream()
            .anyMatch(r -> r.getName().equalsIgnoreCase("Director"));
        if (!isDirector) {
            sql += " AND (r.created_by IN (SELECT eid FROM Org))";
        }

        sql += """
            ) t
            WHERE t.row_index BETWEEN (? - 1) * ? + 1 AND ? * ?
        """;

        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, user.getEmployee().getId());
        stm.setInt(2, pageIndex);
        stm.setInt(3, pageSize);
        stm.setInt(4, pageIndex);
        stm.setInt(5, pageSize);

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


public int countRequestsByRole(User user) {
    int count = 0;
    try {
        String sql = """
            WITH Org AS (
                SELECT e.eid, e.did, 0 AS lvl
                FROM Employee e WHERE e.eid = ?
                UNION ALL
                SELECT c.eid, c.did, o.lvl + 1
                FROM Employee c
                JOIN Org o ON c.supervisorid = o.eid
            )
            SELECT COUNT(*) AS total
            FROM RequestForLeave r
            INNER JOIN Employee e ON e.eid = r.created_by
        """;

        boolean isDirector = user.getRoles().stream()
            .anyMatch(r -> r.getName().equalsIgnoreCase("Director"));
        if (!isDirector) {
            sql += " WHERE r.created_by IN (SELECT eid FROM Org)";
        }

        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, user.getEmployee().getId());
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            count = rs.getInt("total");
        }
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        closeConnection();
    }
    return count;
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
            SELECT r.rid, r.[from], r.[to], r.reason, r.status,r.type,
                   e.ename AS created_name, d.dname AS division_name,
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
             
            Employee createdBy = new Employee();
            createdBy.setName(rs.getString("created_name"));
            Division d = new Division();
            d.setDname(rs.getString("division_name"));
            createdBy.setDiv(d);
            r.setCreated_by(createdBy);

            if (rs.getString("processed_name") != null) {
                Employee p = new Employee();
                p.setName(rs.getString("processed_name"));
                r.setProcessed_by(p);
            }
            return r;
        }
    } catch (SQLException ex) {
        Logger.getLogger(RequestForLeaveDBContext.class.getName()).log(Level.SEVERE, null, ex);
    } 
//    finally {
//        closeConnection();
//    }
    return null;
}

}
