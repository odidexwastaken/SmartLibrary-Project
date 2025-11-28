# SmartLibrary - Kütüphane Yönetim Sistemi

Bu proje; Java, SQLite ve JDBC kullanılarak geliştirilmiş masaüstü konsol uygulamasıdır.

## Kurulum ve Çalıştırma

Projenin çalışabilmesi için SQLite JDBC sürücüsünün projeye dahil edilmesi gerekmektedir. Gerekli `.jar` dosyası proje içerisinde **`lib`** klasöründe mevcuttur.

**Adımlar:**
1. Projeyi IntelliJ IDEA (veya Eclipse) ile açın.
2. **File > Project Structure > Libraries** menüsüne gidin.
3. `+` butonuna basıp **Java**'yı seçin.
4. Proje klasörü içindeki **`lib/sqlite-jdbc-xxxx.jar`** dosyasını seçip ekleyin.
5. `Main.java` dosyasını çalıştırın.

*Not: Veritabanı dosyası (`library.db`), program ilk çalıştırıldığında proje dizininde otomatik olarak oluşturulacaktır.*

🛠 Proje Mimarisi ve Kod İşleyişi
Proje, nesne yönelimli programlama (OOP) mantığına uygun olarak parçalara ayrılmıştır. Bütün kodlar tek bir dosyada toplanmak yerine, görevlerine göre sınıflara (class) bölünmüştür.

Aşağıda sınıfların ne işe yaradığı ve sistemin nasıl çalıştığı özetlenmiştir:

1. Varlık Sınıfları (Entities)
Bu sınıflar veritabanındaki tabloların Java tarafındaki karşılığıdır. İçlerinde herhangi bir işlem yapılmaz, sadece veriyi taşımak için kullanılırlar.

Book Sınıfı: Kitapların id, başlık, yazar ve basım yılı bilgilerini tutar.

Student Sınıfı: Öğrencilerin id, isim ve bölüm bilgilerini tutar.

Loan Sınıfı: Ödünç alma işlemlerini tutar. Hangi kitabın, hangi öğrenci tarafından, ne zaman alındığını ve teslim edilip edilmediğini saklar.

2. Veritabanı Yönetimi (Database Class)
Database Sınıfı:

SQLite bağlantısını kuran ana sınıftır.

Program her çalıştığında library.db dosyasının olup olmadığını kontrol eder.

Eğer dosya yoksa, createTables() metodu devreye girer ve books, students, loans tablolarını otomatik olarak oluşturur. Bu sayede harici bir SQL editörü kullanmaya gerek kalmaz.

3. İşlem Sınıfları (Repositories)
Veritabanı ile iletişim kuran, SQL sorgularının (INSERT, SELECT, UPDATE) yazıldığı kısımdır. Main sınıfı veritabanına doğrudan erişmez, bu sınıfları aracı olarak kullanır.

BookRepository: Kitap ekleme (add) ve tüm kitapları listeleme (getAll) işlemlerini yapar.

StudentRepository: Öğrenci kaydı oluşturma ve listeleme işlemlerini yapar.

LoanRepository:

isBookBorrowed(): Bir kitap ödünç verilmeden önce, bu metot çalışır ve kitabın şu an başkasında olup olmadığını kontrol eder.

lendBook(): Kitabı öğrenciye ödünç verir (Veritabanına kayıt atar).

returnBook(): Kitap iade edildiğinde, ilgili kaydı bulur ve iade tarihini günceller.

4. Ana Sınıf (Main)
Uygulamanın giriş noktasıdır.

Kullanıcıya konsol üzerinden bir menü sunar.

Scanner ile kullanıcının seçimini alır.

Switch-Case yapısı ile kullanıcının seçimine göre ilgili Repository sınıfını çağırır.

Kullanıcı "Çıkış" diyene kadar program while(true) döngüsü içinde çalışmaya devam eder.
