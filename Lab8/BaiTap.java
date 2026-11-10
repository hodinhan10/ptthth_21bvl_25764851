package Lab8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BaiTap {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement them = null;
        PreparedStatement sua = null;
        PreparedStatement xoa = null;
        PreparedStatement tim = null;
        ResultSet rs = null;

        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:Lab8/data/nhanvien.db";
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS NhanVien (
                        id INTEGER PRIMARY KEY,
                        ten TEXT NOT NULL,
                        chuc_vu TEXT
                    )
                    """);

            stmt.executeUpdate("""
                    INSERT OR IGNORE INTO NhanVien (id, ten, chuc_vu)
                    VALUES (1, 'An', 'Kế toán')
                    """);
            stmt.executeUpdate("""
                    INSERT OR IGNORE INTO NhanVien (id, ten, chuc_vu)
                    VALUES (2, 'Bình', 'Kỹ thuật')
                    """);
            stmt.executeUpdate("""
                    INSERT OR IGNORE INTO NhanVien (id, ten, chuc_vu)
                    VALUES (3, 'Chi', 'Nhân sự')
                    """);

            rs = stmt.executeQuery("""
                    SELECT id, ten, chuc_vu
                    FROM NhanVien
                    """);

            System.out.println("DANH SÁCH NHÂN VIÊN:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | "
                        + rs.getString("ten") + " | " + rs.getString("chuc_vu"));
            }
            rs.close();

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS SanPham (
                        maSP INTEGER PRIMARY KEY,
                        tenSP TEXT NOT NULL,
                        gia REAL CHECK(gia >= 0),
                        soluong INTEGER CHECK(soluong >= 0)
                    )
                    """);
            them = conn.prepareStatement("""
                    INSERT OR IGNORE INTO SanPham (maSP, tenSP, gia, soluong)
                    VALUES (?, ?, ?, ?)
                    """);
            them.setInt(1, 1);
            them.setString(2, "Bút bi");
            them.setDouble(3, 5000);
            them.setInt(4, 100);
            them.executeUpdate();

            them.setInt(1, 2);
            them.setString(2, "Vở");
            them.setDouble(3, 12000);
            them.setInt(4, 50);
            them.executeUpdate();

            them.setInt(1, 3);
            them.setString(2, "Thước");
            them.setDouble(3, 8000);
            them.setInt(4, 30);
            them.executeUpdate();

            rs = stmt.executeQuery("""
                    SELECT maSP, tenSP, gia, soluong
                    FROM SanPham
                    """);
            System.out.println("\nDANH SÁCH SẢN PHẨM:");
            while (rs.next()) {
                System.out.println(rs.getInt("maSP") + " | " + rs.getString("tenSP")
                        + " | " + rs.getDouble("gia") + " | " + rs.getInt("soluong"));
            }
            rs.close();

            sua = conn.prepareStatement("""
                    UPDATE SanPham
                    SET gia = ?, soluong = ?
                    WHERE maSP = ?
                    """);
            sua.setDouble(1, 6000);
            sua.setInt(2, 90);
            sua.setInt(3, 1);
            System.out.println("\nSố sản phẩm đã sửa: " + sua.executeUpdate());
            System.out.println("DANH SÁCH SẢN PHẨM SAU KHI SỬA:");
            rs = stmt.executeQuery("SELECT maSP, tenSP, gia, soluong FROM SanPham ORDER BY maSP");
            while (rs.next()) {
                System.out.println(rs.getInt("maSP") + " | " + rs.getString("tenSP")
                        + " | " + rs.getDouble("gia") + " | " + rs.getInt("soluong"));
            }
            rs.close();

            xoa = conn.prepareStatement("""
                    DELETE FROM SanPham
                    WHERE maSP = ?
                    """);
            xoa.setInt(1, 3);
            System.out.println("\nSố sản phẩm đã xóa: " + xoa.executeUpdate());
            System.out.println("DANH SÁCH SẢN PHẨM SAU KHI XÓA:");
            rs = stmt.executeQuery("SELECT maSP, tenSP, gia, soluong FROM SanPham ORDER BY maSP");
            while (rs.next()) {
                System.out.println(rs.getInt("maSP") + " | " + rs.getString("tenSP")
                        + " | " + rs.getDouble("gia") + " | " + rs.getInt("soluong"));
            }
            rs.close();

            tim = conn.prepareStatement("""
                    SELECT maSP, tenSP, gia, soluong
                    FROM SanPham
                    WHERE tenSP LIKE ?
                    """);
            tim.setString(1, "%Bút%");
            rs = tim.executeQuery();
            System.out.println("\nKẾT QUẢ TÌM KIẾM:");
            while (rs.next()) {
                System.out.println(rs.getInt("maSP") + " | " + rs.getString("tenSP")
                        + " | " + rs.getDouble("gia") + " | " + rs.getInt("soluong"));
            }

            conn.commit();
            System.out.println("Đã lưu dữ liệu.");
        } catch (ClassNotFoundException e) {
            System.out.println("Chưa thêm thư viện SQLite JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Đã hủy giao dịch.");
                } catch (SQLException ex) {
                    System.out.println("Lỗi hủy giao dịch: " + ex.getMessage());
                }
            }
        } finally {
            dong(rs);
            dong(tim);
            dong(xoa);
            dong(sua);
            dong(them);
            dong(stmt);
            dong(conn);
        }
    }

    private static void dong(AutoCloseable taiNguyen) {
        if (taiNguyen != null) {
            try {
                taiNguyen.close();
            } catch (Exception e) {
                System.out.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }
}
