import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

// ==========================================
// 1. ENTITY SINIFLARI (Sadeleştirildi)
// ==========================================

class Book {
    private final int id;
    private final String title;
    private final String author;
    private final int year;

    public Book(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    @Override
    public String toString() {
        return id + " - " + title + " (" + author + ", " + year + ")";
    }
}

class Student {
    private final int id;
    private final String name;
    private final String department;

    public Student(int id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + department + ")";
    }
}

class Loan {
    private final int id;
    private final int bookId;
    private final int studentId;
    private final String dateBorrowed;
    private final String dateReturned;

    public Loan(int id, int bookId, int studentId, String dateBorrowed, String dateReturned) {
        this.id = id;
        this.bookId = bookId;
        this.studentId = studentId;
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
    }

    @Override
    public String toString() {
        return "İşlem ID: " + id + " | Kitap ID: " + bookId + " | Öğrenci ID: " + studentId +
                " | Alış: " + dateBorrowed + " | Teslim: " + (dateReturned == null ? "Teslim Edilmedi" : dateReturned);
    }
}

// ==========================================
// 2. DATABASE BAĞLANTISI
// ==========================================

class Database {
    private static final String URL = "jdbc:sqlite:library.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
        } catch (Exception e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String sqlBooks = "CREATE TABLE IF NOT EXISTS books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT, "
                + "author TEXT, "
                + "year INTEGER);";

        String sqlStudents = "CREATE TABLE IF NOT EXISTS students ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "department TEXT);";

        String sqlLoans = "CREATE TABLE IF NOT EXISTS loans ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "bookId INTEGER, "
                + "studentId INTEGER, "
                + "dateBorrowed TEXT, "
                + "dateReturned TEXT);";

        try (Connection conn = connect()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sqlBooks);
                    stmt.execute(sqlStudents);
                    stmt.execute(sqlLoans);
                }
            }
        } catch (SQLException e) {
            System.out.println("Tablo hatası: " + e.getMessage());
        }
    }
}

// ==========================================
// 3. REPOSITORY SINIFLARI (İşlemler)
// ==========================================

class BookRepository {
    public void add(String title, String author, int year) {
        String sql = "INSERT INTO books(title, author, year) VALUES(?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setInt(3, year);
            pstmt.executeUpdate();
            System.out.println(">> Kitap eklendi.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Book> getAll() {
        ArrayList<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("year")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}

class StudentRepository {
    public void add(String name, String department) {
        String sql = "INSERT INTO students(name, department) VALUES(?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, department);
            pstmt.executeUpdate();
            System.out.println(">> Öğrenci kaydedildi.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Student> getAll() {
        ArrayList<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("department")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}

class LoanRepository {
    public boolean isBookBorrowed(int bookId) {
        String sql = "SELECT count(*) FROM loans WHERE bookId = ? AND dateReturned IS NULL";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void lendBook(int bookId, int studentId, String date) {
        if (isBookBorrowed(bookId)) {
            System.out.println("HATA: Bu kitap şu an başkasında!");
            return;
        }
        String sql = "INSERT INTO loans(bookId, studentId, dateBorrowed) VALUES(?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, studentId);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
            System.out.println(">> Kitap öğrenciye verildi.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void returnBook(int bookId, String dateReturned) {
        String sql = "UPDATE loans SET dateReturned = ? WHERE bookId = ? AND dateReturned IS NULL";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dateReturned);
            pstmt.setInt(2, bookId);
            int affected = pstmt.executeUpdate();
            if (affected > 0) System.out.println(">> İade işlemi başarılı.");
            else System.out.println(">> Hata: Bu kitap zaten bizde veya kayıt yok.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Loan> getAll() {
        ArrayList<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Loan(rs.getInt("id"), rs.getInt("bookId"), rs.getInt("studentId"), rs.getString("dateBorrowed"), rs.getString("dateReturned")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}

// ==========================================
// 4. MAIN SINIFI
// ==========================================

public class Main {
    public static void main(String[] args) {
        Database.createTables();
        Scanner scanner = new Scanner(System.in);

        BookRepository bookRepo = new BookRepository();
        StudentRepository studentRepo = new StudentRepository();
        LoanRepository loanRepo = new LoanRepository();

        System.out.println("=== SMART LIBRARY SİSTEMİ ===");

        while (true) {
            System.out.println("\n[1] Kitap Ekle");
            System.out.println("[2] Kitapları Listele");
            System.out.println("[3] Öğrenci Ekle");
            System.out.println("[4] Öğrencileri Listele");
            System.out.println("[5] Ödünç Ver");
            System.out.println("[6] Ödünçleri Gör");
            System.out.println("[7] İade Al");
            System.out.println("[0] Çıkış");
            System.out.print("Seçim: ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Sadece sayı gir babuş.");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Kitap Adı: "); String t = scanner.nextLine();
                    System.out.print("Yazar: "); String a = scanner.nextLine();
                    System.out.print("Yıl: "); int y = scanner.nextInt();
                    bookRepo.add(t, a, y);
                    break;
                case 2:
                    for (Book b : bookRepo.getAll()) System.out.println(b);
                    break;
                case 3:
                    System.out.print("Ad Soyad: "); String n = scanner.nextLine();
                    System.out.print("Bölüm: "); String d = scanner.nextLine();
                    studentRepo.add(n, d);
                    break;
                case 4:
                    for (Student s : studentRepo.getAll()) System.out.println(s);
                    break;
                case 5:
                    System.out.print("Kitap ID: "); int bid = scanner.nextInt();
                    System.out.print("Öğrenci ID: "); int sid = scanner.nextInt();
                    System.out.print("Tarih: "); String date = scanner.next();
                    loanRepo.lendBook(bid, sid, date);
                    break;
                case 6:
                    for (Loan l : loanRepo.getAll()) System.out.println(l);
                    break;
                case 7:
                    System.out.print("İade Kitap ID: "); int rbid = scanner.nextInt();
                    System.out.print("İade Tarihi: "); String rdate = scanner.next();
                    loanRepo.returnBook(rbid, rdate);
                    break;
                case 0:
                    System.out.println("Bye.");
                    return;
                default:
                    System.out.println("Geçersiz işlem.");
            }
        }
    }
}
