#  SmartLibrary - Kütüphane Yönetim Sistemi

Bu proje; **Java**, **SQLite** ve **JDBC** kullanılarak geliştirilmiş, nesne yönelimli programlama (OOP) prensiplerine dayalı bir masaüstü konsol uygulamasıdır.

---

##  Kurulum ve Çalıştırma

Projenin çalışması için `sqlite-jdbc` kütüphanesinin aktif edilmesi gerekir.

1.  Projeyi IntelliJ IDEA ile açın.
2.  Sol taraftaki proje dosyaları arasında **`lib`** klasörünü açın.
3.  İçindeki **`sqlite-jdbc-xxxx.jar`** dosyasına **SAĞ TIKLAYIN**.
4.  Açılan menünün en alt kısımlarında **"Add as Library..."** seçeneğine tıklayın.
5.  Gelen küçük pencerede **OK** butonuna basın.
6.  Artık `Main.java` dosyasını çalıştırabilirsiniz.

 **Not:** Veritabanı dosyası (`library.db`), program ilk çalıştırıldığında proje dizininde otomatik olarak oluşturulacaktır. Herhangi bir ekstra SQL kurulumuna gerek yoktur.

---

##  Proje Mimarisi ve Kod İşleyişi

Proje, temiz kod (clean code) prensipleri gözetilerek **OOP** mantığına uygun parçalara ayrılmıştır. Kodlar tek bir dosyada toplanmak yerine, görevlerine göre sınıflara (Class) bölünmüştür.

Aşağıda sınıfların görevleri özetlenmiştir:

### 1. Varlık Sınıfları (Entities)
Bu sınıflar veritabanındaki tabloların Java tarafındaki karşılığıdır. İçlerinde herhangi bir mantıksal işlem yapılmaz, sadece veriyi taşımak (Data Transfer) için kullanılırlar.
* **`Book` Sınıfı:** Kitapların `id`, `başlık`, `yazar` ve `basım yılı` bilgilerini tutar.
* **`Student` Sınıfı:** Öğrencilerin `id`, `isim` ve `bölüm` bilgilerini tutar.
* **`Loan` Sınıfı:** Ödünç alma işlemlerini tutar. Hangi kitabın, hangi öğrenci tarafından, ne zaman alındığını saklar.

### 2. Veritabanı Yönetimi (Database Class)
* **`Database` Sınıfı:** SQLite bağlantısını kuran ana sınıftır.
* Program her çalıştığında `library.db` dosyasının olup olmadığını kontrol eder.
* Eğer dosya yoksa, **`createTables()`** metodu devreye girer; `books`, `students` ve `loans` tablolarını otomatik olarak oluşturur.

### 3. İşlem Sınıfları (Repositories)
Veritabanı ile iletişim kuran, SQL sorgularının (`INSERT`, `SELECT`, `UPDATE`) yazıldığı katmandır. `Main` sınıfı veritabanına doğrudan erişmez, bu sınıfları aracı olarak kullanır.

* **`BookRepository`:**
    * Kitap ekleme (`add`) ve tüm kitapları listeleme (`getAll`) işlemlerini yapar.
* **`StudentRepository`:**
    * Öğrenci kaydı oluşturma ve listeleme işlemlerini yapar.
* **`LoanRepository`:**
    * `isBookBorrowed()`: Bir kitap ödünç verilmeden önce çalışır, kitabın başkasında olup olmadığını kontrol eder.
    * `lendBook()`: Kitabı öğrenciye ödünç verir.
    * `returnBook()`: Kitap iade edildiğinde ilgili kaydı günceller.

### 4. Ana Sınıf (Main)
Uygulamanın giriş noktasıdır.
* Kullanıcıya konsol üzerinden bir menü sunar.
* `Scanner` ile kullanıcının seçimini alır.
* `Switch-Case` yapısı ile kullanıcının seçimine göre ilgili Repository sınıfını çağırır.
