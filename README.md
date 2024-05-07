# Planify: A Personal Event Organizer


<img width="1412" alt="image" src="https://github.com/ShawnG134/Planify/assets/168505455/68380c4c-166f-4d37-a853-78cc51927aee">

## Quick Start Guide

Welcome to Planify! This guide will help you get started with installation and familiarize you with the key features of the application. Planify helps you organize your time efficiently by offering detailed visual summaries of your productivity and easy event management.

### Installation Instructions

#### On Mac
1. **Install Docker**:
  - Visit the [Docker website](https://hub.docker.com/) to download the installer.
  - Install and launch Docker. Ensure it's running before proceeding.
2. **Run the Server**:
  - Download the server image: `docker pull orasean/ktor-server`
  - Start the server: `docker run -p 8080:8080 orasean/ktor-server`
3. **Install the App**:
  - Open the `.dmg` file and drag the application to the Applications folder.
  - Launch Planify from the Launchpad.
  - Ensure the server is running before starting the app.

#### On Windows
1. **Install Docker**:
  - Download Docker from [here](https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe).
  - Install and open Docker, ensuring it is active.
2. **Run the Server**:
  - Download the server image: `docker pull orasean/ktorserver-image`
  - Start the server: `docker run -p 8080:8080 orasean/ktorserver-image`
3. **Install the App**:
  - Execute the `.msi` installer and follow the prompts.
  - Launch Planify from the Start Menu or the installation directory.
  - The server should be active prior to using the app.

### Navigating Planify

#### Home Section
- **Welcome Home**: Displays the current date and time.
- **Time Bars**: Visual indicators of time elapsed today, this week, month, and year.
- **Daily Calendar**: Shows today's scheduled events.

#### Calendar
- Default view is the monthly calendar.
- Switch between daily and monthly views using the top center buttons.
- Navigate through dates using the arrows in the top right corner.

#### Summary
- **Task Progress**: Displays completed tasks as a percentage.
- **Weekly Focus Stats**: Bar chart summarizing productive hours.
- **Achievements**: Lists milestones like "Productive Week" and specific achievements based on task type.

#### To-Do List
- **Create and Manage Events**: Add, edit, delete, and mark tasks as completed.
- Events can be categorized and prioritized, with specific start times and durations.

#### Notes
- **Management**: Create, format, and delete notes.
- **Folders**: Organize notes into folders.
- **Rich Formatting Options**: Enhance note presentation with formatting tools.
- **Exports and Imports**: Notes can be exported or imported as HTML files.

#### Hotkeys
- Utilize keyboard shortcuts for various formatting options in notes.

### Support

#### FAQ
- **Adding Events**: Create events in the To-Do List section; the calendar displays them.
- **Using Folders**: To add notes to a folder, open the folder and create a new note inside it.
- **Understanding the Bar Chart**: Each bar represents the total duration of completed tasks for that day.

Feel free to reach out with further questions or feedback on your experience with Planify!
