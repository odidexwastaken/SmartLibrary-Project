import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

// ==========================================
// 1. ENTITY SINIFLARI (Varlıklar)
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

    // update işlemleri için get ler şart
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }

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

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }

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

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getStudentId() { return studentId; }
    public String getDateBorrowed() { return dateBorrowed; }
    public String getDateReturned() { return dateReturned; }

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
// 3. REPOSITORY SINIFLARI
// ==========================================

class BookRepository {
    // ADD
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

    // UPDATE
    public void update(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, year = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getYear());
            pstmt.setInt(4, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // DELETE
    public void delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // GET BY ID
    public Book getById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("year"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // GET ALL
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
    // ADD
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

    // UPDATE
    public void update(Student student) {
        String sql = "UPDATE students SET name = ?, department = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getDepartment());
            pstmt.setInt(3, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // DELETE
    public void delete(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // GET BY ID
    public Student getById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("department"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // GET ALL
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
    // kitap müsait mi
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

    // ADD
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

    // UPDATE
    public void update(Loan loan) {
        String sql = "UPDATE loans SET dateReturned = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loan.getDateReturned());
            pstmt.setInt(2, loan.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // kitap iade
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

    // DELETE
    public void delete(int id) {
        String sql = "DELETE FROM loans WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // GET BY ID
    public Loan getById(int id) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Loan(rs.getInt("id"), rs.getInt("bookId"), rs.getInt("studentId"), rs.getString("dateBorrowed"), rs.getString("dateReturned"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // GET ALL
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
// 4. MAIN SINIFI (Orkestra Şefi)
// ==========================================

public class Main {
    public static void main(String[] args) {
        // uygulama başlarken tabloları oluştur
        Database.createTables(); 
        
        Scanner scanner = new Scanner(System.in);
        BookRepository bookRepo = new BookRepository();
        StudentRepository studentRepo = new StudentRepository();
        LoanRepository loanRepo = new LoanRepository();

        System.out.println("=== SMART LIBRARY SİSTEMİ ===");

        while (true) {
            System.out.println("\n--- MENÜ ---");
            System.out.println("[1] Kitap Ekle");
            System.out.println("[2] Kitapları Listele");
            System.out.println("[3] Öğrenci Ekle");
            System.out.println("[4] Öğrencileri Listele");
            System.out.println("[5] Kitap Ödünç Ver");
            System.out.println("[6] Ödünç Listesini Gör");
            System.out.println("[7] Kitap İade Al");
            System.out.println("[0] Çıkış");
            System.out.print("Seçiminiz: ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // buffer temizleme
            } catch (Exception e) {
                System.out.println("Lütfen sadece sayı giriniz.");
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
                    System.out.println("\n-- Kitaplar --");
                    for (Book b : bookRepo.getAll()) System.out.println(b);
                    break;
                case 3:
                    System.out.print("Ad Soyad: "); String n = scanner.nextLine();
                    System.out.print("Bölüm: "); String d = scanner.nextLine();
                    studentRepo.add(n, d);
                    break;
                case 4:
                    System.out.println("\n-- Öğrenciler --");
                    for (Student s : studentRepo.getAll()) System.out.println(s);
                    break;
                case 5:
                    System.out.print("Kitap ID: "); int bid = scanner.nextInt();
                    System.out.print("Öğrenci ID: "); int sid = scanner.nextInt();
                    System.out.print("Tarih (GG.AA.YYYY): "); String date = scanner.next();
                    loanRepo.lendBook(bid, sid, date);
                    break;
                case 6:
                    System.out.println("\n-- Ödünç İşlemleri --");
                    for (Loan l : loanRepo.getAll()) System.out.println(l);
                    break;
                case 7:
                    System.out.print("İade Edilen Kitap ID: "); int rbid = scanner.nextInt();
                    System.out.print("İade Tarihi (GG.AA.YYYY): "); String rdate = scanner.next();
                    loanRepo.returnBook(rbid, rdate);
                    break;
                case 0:
                    System.out.println("Çıkış yapılıyor...");
                    return;
                default:
                    System.out.println("Geçersiz seçim, tekrar deneyin.");
            }
        }
    }
}
