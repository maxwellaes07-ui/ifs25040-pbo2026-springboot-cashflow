# maxwell-pbo2026-springboot-cashflow

Aplikasi web pencatat keuangan pribadi (Pemasukan / Pengeluaran + saldo) dengan
**Spring Boot 4.1.2**, **Vaadin 25.3.4**, **Java 25**, dan **Clean Architecture**.
Data disimpan in-memory (hilang saat aplikasi di-restart).

- Group: `org.delcom`
- Package: `org.delcom.maxwellpbo2026springbootcashflow`

## Menjalankan

```powershell
.\mvnw.cmd spring-boot:run
```
(Linux/macOS: `./mvnw spring-boot:run`). Buka http://localhost:8080 (port dari `APP_PORT` di `.env`).

Build JAR (wajib profil `production`):

```powershell
.\mvnw.cmd clean package -Pproduction
java -jar target\maxwell-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar
```

Test + laporan coverage Jacoco (`target/site/jacoco/index.html`):

```powershell
.\mvnw.cmd clean verify
```

## Struktur

```
src/main/java/org/delcom/maxwellpbo2026springbootcashflow/
├── App.java, DotEnv.java
├── config/AppConfig.java              # composition root (@Bean)
├── domain/
│   ├── entity/      Transaction, TransactionType, SortOption
│   ├── exception/   DomainException, EntityNotFoundException, ValidationException
│   └── repository/  IRepository<T, ID>, ITransactionRepository
├── usecase/FinanceUseCase.java        # tanpa I/O UI
├── adapter/
│   ├── repository/  InMemoryRepository<T, ID>, TransactionRepository
│   └── presenter/   FinancePresenter  # format Rp 1,000,000.00
└── framework/view/FinanceView.java    # Vaadin @Route("")
```

## Memakai username lain

Ganti `maxwell` dengan username Anda di: nama folder proyek, `pom.xml`
(`artifactId`, `name`), `application.yaml` (`spring.application.name`), nama folder
package `src/**/org/delcom/maxwellpbo2026springbootcashflow`, dan semua baris
`package` / `import`.
