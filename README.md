# SmartLibrary - Kütüphane Yönetim Sistemi

Bu proje; Java, SQLite ve JDBC kullanılarak geliştirilmiş masaüstü konsol uygulamasıdır.

## 🚀 Kurulum ve Çalıştırma (Önemli)

Projenin çalışabilmesi için SQLite JDBC sürücüsünün projeye dahil edilmesi gerekmektedir. Gerekli `.jar` dosyası proje içerisinde **`lib`** klasöründe mevcuttur.

**Adımlar:**
1. Projeyi IntelliJ IDEA (veya Eclipse) ile açın.
2. **File > Project Structure > Libraries** menüsüne gidin.
3. `+` butonuna basıp **Java**'yı seçin.
4. Proje klasörü içindeki **`lib/sqlite-jdbc-xxxx.jar`** dosyasını seçip ekleyin.
5. `Main.java` dosyasını çalıştırın.

*Not: Veritabanı dosyası (`library.db`), program ilk çalıştırıldığında proje dizininde otomatik olarak oluşturulacaktır.*
