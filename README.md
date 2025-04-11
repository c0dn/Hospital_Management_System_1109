# Hospital Management System (HMS) - CSC1109 School project

A comprehensive **simulation** of a Hospital Management System built with Java and Gradle. 
This application provides a command-line interface for managing various aspects of hospital operations, including
patient records, staff management, appointments, billing, and insurance claims.

## Features

* **Patient Management:** Register, view, and manage patient information.
* **Appointment Scheduling:** Handle telemedicine appointments.
* **Billing System:** Generate and manage patient bills for various services 
* **Insurance Claims:** Create and manage insurance claims.
* **Data Persistence:** Uses JSON files for data storage
* **Text-Based User Interface (TUI):** Interactive command-line interface for different user roles (Patient, Doctor, Clerk).

## Prerequisites

* Java Development Kit (JDK) 21 or later.
* Gradle (The project uses the Gradle wrapper, so it should download automatically if not present).

## Getting Started

1.  **Clone the repository**
    ```bash
    git clone https://github.com/c0dn/Hospital_Management_System_1109.git
    cd Hospital_Management_System_1109
    ```

2.  **Build the Project**
    ```bash
    ./gradlew buildOnly
    # Or just build the JAR
    ./gradlew jar
    ```
    The JAR file will be located in `build/libs/`.

3.  **Run the Application** \
    This command runs the application using the generated JAR file. It copies the `database` directory to the execution directory (`build/libs/`) if it doesn't exist or is empty.
    ```bash
    ./gradlew runJar
    ```

4.  **Run with Clean Data:**
    This command runs the application, ensuring the `database` directory in `build/libs/` exists but removes any existing `.txt` files (presumably data files) before copying the base structure from `database/`.
    ```bash
    ./gradlew runJarCleanSlate
    ```

5.  **Run with Overwritten Data:**
    This command completely deletes the existing `database` directory in `build/libs/` and copies a fresh version from the project's root `database` directory before running the application. **Use with caution as it will delete existing data in the build directory.**
    ```bash
    ./gradlew runJarOverwriteData
    ```

## Running Tests

Execute the JUnit tests:
```bash
./gradlew test
```
Test reports are generated in build/reports/tests/test/.

## Generating Documentation

Generate JavaDoc documentation for the project:
```bash
./gradlew generateJavadoc
```
The documentation will be generated in the `docs/` directory at the project root.

## Key Dependencies

* **JUnit 5**: For unit testing.
* **OkHttp**: For making HTTP requests to external services
* **Jackson**: For JSON data serialization and deserialization.

## Project Structure (Key Packages)

* `org.bee.hms.auth`: Authentication related classes.
* `org.bee.hms.billing`: Classes related to billing and payments.
* `org.bee.hms.claims`: Insurance claim management.
* `org.bee.hms.humans`: Data models for Patients, Doctors, Nurses, Clerks.
* `org.bee.hms.insurance`: Insurance provider models.
* `org.bee.hms.medical`: Medical entities like Visits, Consultations, Medications, Codes.
* `org.bee.hms.policy`: Insurance policy structure and logic.
* `org.bee.hms.telemed`: Telemedicine appointment and session management.
* `org.bee.hms.wards`: Ward and bed management classes.
* `org.bee.controllers`: Controller classes handling application logic.
* `org.bee.pages`: UI pages for different user roles and functions.
* `org.bee.ui`: Core classes for the text-based user interface framework.
* `org.bee.utils`: Utility classes for CSV, JSON handling, data generation, etc.
* `org.bee.tests`: Unit tests for various components.
