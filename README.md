# PESCD System

> Web application that automates the credit workflow of UFSCar's Supervised Teaching Internship Program.

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)

## Overview

The **PESCD** (Supervised Teaching Internship Program) is a mandatory requirement for PhD students at UFSCar's Computer Science graduate program — between 2 and 3 semesters performing undergraduate teaching activities. The control of work plans, reports, and evaluations is currently handled manually.

This project fully automates that workflow, providing dedicated areas for each of the 5 user roles involved, full status-change traceability, PDF document uploads, and granular role-based access control.

## Roles and features

| Role | Main features |
|---|---|
| **Visitor** | Public listing of active offerings and number of enrolled students |
| **Student** | Submit work plan, supporting documentation, or final report; view participation history across semesters |
| **Secretary** | Create offerings, manage enrollments (CRUD + CSV import), close offerings |
| **Supervising Professor** | Approve work plans and final reports of supervised students |
| **Responsible Professor** | Issue the final evaluation of the internship or documentation; close the offering |
| **Administrator** | Manage users and roles |

## Tech stack

**Backend**
- Java 17 + Spring Boot 4
- Spring MVC, Spring Data JPA, Spring Security
- Thymeleaf + `thymeleaf-extras-springsecurity6`
- MySQL 8 / Hibernate
- Maven, Lombok

**Frontend**
- HTML5, CSS3, JavaScript

**Architecture**: Classic MVC with clear separation across Controller → Service → Repository → Domain. Authorization checks performed at the Service layer for defense in depth. POST-Redirect-GET pattern applied to all forms.

## Running locally

Requires Java 17+, Maven, and MySQL.

```bash
# 1. Make sure MySQL is running — the db_pescd database is created automatically
# 2. Adjust credentials in src/main/resources/application.properties if needed
# 3. Build and run:

./mvnw spring-boot:run
```

The app becomes available at `http://localhost:8080`. Test users are seeded automatically on startup — credentials can be found in `PescdApplication.java`.

## Team

- **Yohan Duarte**
- **Pedro Cappelini**
- **Gustavo Bragaia**
- **Caio Miyashi Ishii**
- **Felipe Betcher**

## Academic context

Project developed for the **Web Software Development I** course of the undergraduate Computer Science program at UFSCar, under the guidance of Prof. [André Takeshi Endo](https://www.lapes.ufscar.br/members/professors/andre-takeshi-endo).