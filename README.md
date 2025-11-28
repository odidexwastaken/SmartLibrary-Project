#  SmartLibrary - Kütüphane Yönetim Sistemi

Bu proje; **Java**, **SQLite** ve **JDBC** kullanılarak geliştirilmiş, nesne yönelimli programlama (OOP) prensiplerine dayalı bir masaüstü konsol uygulamasıdır.

---
### Not

Proje teknik dokümanında **Repository Sınıfları** için talep edilen tüm CRUD işlemleri (`add`, `update`, `delete`, `getById`, `getAll`) altyapı kodlarında eksiksiz olarak **yazılmıştır.**

Ancak proje senaryosundaki **"Uygulama Menü Görevleri"** listesinde; kullanıcıya "Silme" veya "Güncelleme" yaptırılması (UI tarafında) açıkça istenmediği için, bu metodlar arka planda hazır tutulmuş fakat konsol menüsüne dahil edilmemiştir. İstenirse entegre edilebilir durumdadır.

---

##  Kurulum ve Çalıştırma

Projenin çalışması için `sqlite-jdbc` kütüphanesinin aktif edilmesi gerekir.

1.  Projeyi IntelliJ IDEA ile açın.
2.  Sol taraftaki proje dosyaları arasında **`lib`** klasörünü açın.
3.  İçindeki **`sqlite-jdbc-3.51.0.0.jar`** dosyasına **SAĞ TIKLAYIN**.
4.  Açılan menünün en alt kısımlarında **"Add as Library..."** seçeneğine tıklayın.
5.  Gelen küçük pencerede **OK** butonuna basın.
6.  Artık `Main.java` dosyasını çalıştırabilirsiniz.

---

## Proje Mimarisi ve Kod İşleyişi

Proje, **OOP (Nesne Yönelimli Programlama)** prensiplerine uygun olarak katmanlı bir yapıda tasarlanmıştır.

### 1. Varlık Sınıfları (Entities)
Veritabanı tablolarının Java tarafındaki karşılıklarıdır. **Immutability (Değişmezlik)** prensibi gereği değişkenler `final` olarak tanımlanmıştır.
* **`Book`:** Kitap bilgilerini (id, başlık, yazar, yıl) tutar.
* **`Student`:** Öğrenci bilgilerini (id, isim, bölüm) tutar.
* **`Loan`:** Ödünç alma işlem detaylarını tutar.

### 2. Veritabanı Yönetimi (Database Class)
* **`connect()`:** SQLite veritabanına bağlantıyı kurar. (JDBC Sürücüsü burada devreye girer).
* **`createTables()`:** Program her açıldığında tabloları kontrol eder. Eğer yoksa `books`, `students` ve `loans` tablolarını otomatik oluşturur.

### 3. İşlem Sınıfları (Repositories)
Veritabanı ile iletişim kuran (CRUD işlemlerini yapan) ana sınıflardır.

* **`BookRepository` & `StudentRepository`:**
    * `add()`: Yeni kayıt ekler.
    * `getAll()`: Tüm kayıtları listeler.
    * `delete()`: ID'ye göre kayıt siler.
    * `update()`: Var olan kaydı günceller.
    * `getById()`: ID'ye göre tek bir kayıt getirir.

* **`LoanRepository` (Ödünç İşlemleri):**
    * `lendBook()`: Kitabı öğrenciye ödünç verir.
    * `returnBook()`: Kitap iade edildiğinde iade tarihini işler.
    * `isBookBorrowed()`: Kitap başkasına verilmeden önce müsaitlik durumunu kontrol eder.
    * *(Ayrıca standart delete, update, getById fonksiyonlarını da içerir).*

### 4. Ana Sınıf (Main)
Uygulamanın giriş noktasıdır. Kullanıcıya konsol üzerinden bir menü sunar ve seçimlere göre ilgili Repository sınıfını çalıştırır.
