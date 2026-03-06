# Yoga App — Full Stack

Application de gestion de sessions de yoga, avec un back-end Spring Boot et un front-end Angular.

---

## Prérequis

- **Java 17**
- **Maven**
- **Node.js** (v16+) & **npm**
- **MySQL**
- **Angular CLI** (`npm install -g @angular/cli`)

---

## 1. Base de données

Créer la base de données MySQL et exécuter le script d'initialisation :

```sql
CREATE DATABASE yoga;
USE yoga;
```

---

## 2. Back-end (Spring Boot)

### Configuration

Modifier le fichier `back/src/main/resources/application.properties` avec vos identifiants MySQL :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/yoga
spring.datasource.username=<votre_user>
spring.datasource.password=<votre_password>
```

### Installation & lancement

```bash
cd back
mvn clean install
mvn spring-boot:run
```

L'API démarre sur **http://localhost:8080**.

---

## 3. Front-end (Angular)

### Installation

```bash
cd front
npm install
```

### Lancement

```bash
npm start
```

L'application démarre sur **http://localhost:4200**.

---

## 4. Exécuter les tests

### Tests unitaires & d'intégration — Back-end (JUnit + JaCoCo)

```bash
cd back
mvn test
```

Le rapport de couverture JaCoCo est généré dans :

```
back/target/site/jacoco/index.html
```

### Tests unitaires — Front-end (Jest)

```bash
cd front
npm test
```

Le rapport de couverture Jest est généré dans :

```
front/coverage/lcov-report/index.html
```

### Tests E2E — Front-end (Cypress)

Lancer l'application avant de démarrer les tests E2E :

```bash
# Dans un premier terminal
cd front && npm start

# Dans un second terminal
cd front && npm run cypress:open   # mode interactif
# ou
cd front && npm run cypress:run    # mode headless (CI)
```

---

## Structure du projet

```
├── back/        → API Spring Boot (Java 17, Spring Security, JPA, MySQL)
├── front/       → Application Angular 14
└── ressources/  → Script SQL et collection Postman
```

