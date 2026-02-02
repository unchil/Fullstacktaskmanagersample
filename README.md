# Fullstack Task Manager Sample (KMP)
<p >
  <img src="https://img.shields.io/badge/Kotlin-2.2.21-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose_Multiplatform-1.9.3-4285F4?style=flat&logo=jetpackcompose&logoColor=white" alt="Compose Multiplatform">

  <img src="https://img.shields.io/badge/-Android-3DDC84?style=flat&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/-iOS-D3D3D3?style=flat&logo=apple&logoColor=white" alt="iOS">
  <img src="https://img.shields.io/badge/-Desktop-007ACC?style=flat&logo=kotlin&logoColor=white" alt="Desktop">
  <img src="https://img.shields.io/badge/-Web_Wasm-654FF0?style=flat&logo=webassembly&logoColor=white" alt="Web">
</p>
<p >
  <img src="https://img.shields.io/badge/un7datagrid-0.2.9_2-FFA500?style=flat" alt="Version">
  <img src="https://img.shields.io/badge/koalacore-0.10.4-green?style=flat" alt="Version">
</p>

A comprehensive Fullstack Kotlin Multiplatform (KMP) sample project. This project demonstrates a complete data pipeline: from collecting public data to serving it via a REST API and visualizing it on multiple platforms using Compose Multiplatform.

## Project Architecture
The project consists of four main modules:
1. :collectionServer: A dedicated engine for fetching, parsing, and processing public data from external Open APIs.
2. :server: A Ktor-based backend that stores collected data and provides a RESTful API service to clients.
3. :shared: Contains common logic, data models, and business rules shared between the server and the client apps.
4. :composeApp: The UI layer built with Compose Multiplatform, supporting Android, iOS, and Desktop, featuring advanced data visualization.


## Key Features

• Real-time Data Collection: Automated fetching of public data (JSON/XML).

• RESTful Service: Robust API backend providing structured data to various consumers.

• Multiplatform UI: Single codebase for mobile and desktop environments.

• Data Visualization: High-performance data grids and interactive charts.


## Core Multiplatform Libraries

This project leverages several powerful libraries to handle data and UI:

| Library | Role | Description | Url |
| :--- | :--- | :--- | :--- |
| Compose DataGrid | UI Component | unchil/ComposeDataGrid - Used for displaying large-scale collected data in a highly customizable table format. | https://unchil.github.io/ComposeDataGrid/ |
| KoalaPlot | Visualization | A Compose Multiplatform charting library used to render statistical data (Line, Bar, Pie charts). | https://koalaplot.github.io/ | 
| Ktor | Networking | Handles HTTP requests for both the collection server (fetching) and the client app (consuming). | https://ktor.io/ |
| Exposed| Kotlin SQL database library  | a lightweight ORM (using DAO) and type-safe SQL (using DSL). | https://www.jetbrains.com/help/exposed/home.html |


## Screenshots

|          ScreenShot [ Browser(Safari,Chrome)/Desktop(macOS 26.2)/iOS(26.2)/Android(api 36.0) ]          |
|:-------------------------------------------------------------------------------------------------------:|
| ![ScreenShot](https://github.com/unchil/Fullstacktaskmanagersample/raw/main/screenshot/screenshot2.png) | 
|    ![ScreenShot](https://github.com/unchil/Fullstacktaskmanagersample/raw/main/screenshot/full.gif)     | 

## Setup & Installation
1. GitHub Packages Configuration
   This project uses ComposeDataGrid, which is hosted on GitHub Packages. You must configure your local.properties or environment variables to authenticate.
   In your settings.gradle.kts:


```kotlin
maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/unchil/ComposeDataGrid")
    credentials {
        username = System.getenv("GPR_USER") // Your GitHub Username
        password = System.getenv("GPR_KEY")  // Your GitHub Personal Access Token (PAT)
    }
}
```

2. Environment Variables

   Ensure you have the following keys set in your system:

   • GPR_USER: Your GitHub ID.

   • GPR_KEY: A GitHub PAT with read:packages permission.

3. Running the Project

   • Run Backend Server: ./gradlew :server:run

   • Run Android App: ./gradlew :composeApp:installDebug

   • Run Desktop App: ./gradlew :composeApp:run

## Tech Stack
• Language: Kotlin 2.x

• UI Framework: Compose Multiplatform

• Backend: Ktor

• Concurrency: Kotlin Coroutines & Flow

• Build Tool: Gradle (Kotlin DSL)


Created for demonstrating Public Data integration with Kotlin Multiplatform.
