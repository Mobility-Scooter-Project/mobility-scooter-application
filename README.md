# mobility-scooter-application

This README is a high level overview of the mobile app. For a more in-depth technical introduction, please see this document: https://docs.google.com/document/d/1W_scMhpFDm7tW4pXxeFg1GmnmTtC6k02_7rlP6oJ3j0/edit?usp=sharing

## Table of Contents
- [Introduction](#introduction)
- [Purpose](#purpose)
- [Features](#features)
  - [User Authentication](#user-authentication)
  - [Video Recording](#video-recording)
  - [Session History Kotlin MVVM](#session-history-kotlin-mvvm)
  - [Video Analysis and Pose Estimation](#video-analysis-and-pose-estimation)
  - [Accessibility](#accessibility)
- [Implementation](#implementation)
  - [User Authentication](#user-authentication)
  - [Video Recording and Storage](#video-recording-and-storage)
  - [Session History Using Kotlin MVVM](#session-history-using-kotlin-mvvm)
  - [Server and Data Classification](#server-and-data-classification)
- [Installation](#installation)
- [Usage](#usage)
- [Support](#support)
- [Project Status](#project-status)


## Introduction
In today's aging global population, mobility scooters are not just a convenience but a necessity for maintaining the independence and quality of life of the elderly and those with mobility challenges. Although insurance often covers these mobility aids, their usage rate remains surprisingly low. One of the primary reasons for this underutilization is the fear and apprehension users have when it comes to operating these vehicles, primarily due to safety concerns.

## Purpose
The core objective of this project is to bridge the existing safety assessment gap for mobility scooter users. Mobility scooters, indispensable for maintaining a good quality of life, come with their own set of risks. These risks are multi-faceted, arising from the users' driving skills, the environment in which these scooters are operated, and the specific safety features of different scooter models. This project aims to offer a comprehensive evaluation system that assesses all these factors, thereby alleviating safety concerns and encouraging more widespread use of mobility scooters.

## Features
### User Authentication
- Utilizes Firebase Authentication for secure login and signup processes.
- Allows for password recovery and account management.

### Video Recording
- Provides a user-friendly interface to record driving sessions.
- Records high-quality video that can be later analyzed for safety assessments.
- Automatically saves the videos in a secure cloud storage.

### Session History Kotlin MVVM
- Implemented using Kotlin and the MVVM architecture for optimal performance and scalability.
- Allows users to view a history of their driving sessions, complete with metadata like date, start time, and session length.
- Uses Firebase Firestore to fetch and display session histories, ordered by the latest sessions for easy access.

### Video Analysis and Pose Estimation
- Employs advanced machine learning algorithms for post-analysis of recorded driving sessions.
- Provides pose estimation to assess the user's posture and alignment during the drive, which can be critical for safety.

## Implementation

### User Authentication
- Integrated Firebase Authentication to handle user signup, login, and password recovery processes.
- Supports multiple authentication methods, including email/password and social media accounts.

### Video Recording and Storage
- Leveraged CameraX API for improved video recording performance and user experience.
- Employed AES256-GCM HKDF 4KB encryption to ensure the secure storage of recorded videos.
- Videos are stored on Firestore, facilitating easy retrieval for subsequent use and analysis.

### Session History Using Kotlin MVVM
- Utilized Kotlin and the Model-View-ViewModel (MVVM) architecture for efficient management of data and UI.
- Real-time session history retrieval and display are achieved through Firebase Firestore.
- Sorting of sessions by recency is made possible by Firestore's query capabilities.

### Server and Data Classification
- Developed a secure server environment on a Jetstream2 VM instance using Flask.
- Communication is encrypted via HTTPS and authenticated through SSL certificates.
- Data classification is carried out using a pre-trained TensorFlow model on the server side.
- 
### Accessibility
- Developed with a focus on accessibility to make the app usable for people with varying abilities.
- Elements like easily readable fonts, contrasting color schemes, and intuitive navigation enhance accessibility.
- App is designed to be fully compatible with Android Talkback feature.


## Installation
[]

## Usage
[]


## Support
[]

## Project Status
- Enhance user-to-doctor communication for more personalized safety recommendations.
- Collect more data to improve the machine learning models for better driving analysis.

## Task Updates
<table style="border-top: solid 1px; border-left: solid 1px; border-right: solid 1px; border-bottom: solid 1px">
    <thead>
        <tr>
            <th rowspan=1 style="text-align: center; border-right: solid 1px">Task</th>
            <th colspan=3 style="text-align: center; border-right: solid 1px">Description</th>
            <th colspan=1 style="text-align: center; border-right: solid 1px">Assigned To</th>
            <th colspan=1 style="text-align: center; border-right: solid 1px">Completed?</th>
            <th colspan=1 style="text-align: center; border-right: solid 1px">Tested?</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Add Delete Video Feature</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Allow users to delete previous sessions.</td>
            <td style="text-align: center; border-right: solid 1px">Andrew</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Verify User's Position Before Recording</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Create a screen before a user records that asks them to send a screenshot of their body to the server.</td>
            <td style="text-align: center; border-right: solid 1px">Justin</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Add Dark Mode</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Implement a Dark Mode Feature.</td>
            <td style="text-align: center; border-right: solid 1px">Alvan</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Make Screens Scale</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Make each screen scale with different screen sizes rather than stay a fixed size.</td>
            <td style="text-align: center; border-right: solid 1px">Kenia</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Restyle Login & Register Screens</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Restyle the login and reigster screens to be more visually appealing.</td>
            <td style="text-align: center; border-right: solid 1px">Justin & Alvan</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
            <td style="text-align: center; border-right: solid 1px">No</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Thumbnail Bug</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Fix the bug where some thumbnails are not created properly.</td>
            <td style="text-align: center; border-right: solid 1px">Andrew</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Hamburger Menu & Logout Feature</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Implement a hamburger menu that appears from the side and add a feature for the users to log out.</td>
            <td style="text-align: center; border-right: solid 1px">Justin</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Fix Bottom Nav Bar</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Make nav bar persistent rather than hard coded in each screen.</td>
            <td style="text-align: center; border-right: solid 1px">Justin</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Migrate Server</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Migrate Flask server from GCP to Jetstream2.</td>
            <td style="text-align: center; border-right: solid 1px">Melvin</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Implement Talkback Compatibility</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Add content descriptions to each UI element for the screen reader.</td>
            <td style="text-align: center; border-right: solid 1px">Andrew</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Fix Permissions</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Refactor the way the app is asking for user's permissions.</td>
            <td style="text-align: center; border-right: solid 1px">Alvan</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
        </tr>
        <tr>
            <td style="text-align: center; border-right: solid 1px">Fix Crashing & Freezing Issues</td>
            <td colspan=3 style="text-align: center; border-right: solid 1px">Stop the app from freezing and crashing when the drive menu is opened.</td>
            <td style="text-align: center; border-right: solid 1px">Alvan + Melvin</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
            <td style="text-align: center; border-right: solid 1px">Yes</td>
        </tr>
    </tbody>
</table>
