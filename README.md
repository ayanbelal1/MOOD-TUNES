# MoodTunes - Mood-Based Music Player

## Overview
MoodTunes is a Java-based desktop application that provides a personalized music experience based on your mood. It features both online (YouTube) and offline music playback capabilities, along with mood tracking and user profiles.

## Features
- Mood-based music selection
- YouTube integration for online music playback
- Local MP3 file playback
- User authentication system
- Personal music library management
- User profile with mood history
- Intuitive GUI interface

## Prerequisites
- Java Development Kit (JDK) 17 or higher
- MySQL Database Server
- MySQL Connector/J 9.3.0
- JLayer 1.0.1 (jl1.0.jar) for MP3 playback

## Installation

1. **Database Setup**
   - Install MySQL Server
   - Create a new database
   - Run the SQL script to set up the database schema:
   ```bash
   mysql -u your_username -p your_database_name < moodbuddy_db.sql
   ```
1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd project\ ver--2