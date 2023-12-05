# Instruction on Installation and App Running

## On Mac

### Install Docker to Run Server:

 1. Go to Docker website: Download Link for [Mac](https://hub.docker.com/)
 2. Install Docker
 3. Open Docker (Ensure Docker is on)
 4. Run the following two commend:
	 a. Download the server
    $ `docker pull orasean/ktor-server` 
    b. Run the server in the docker
    $ `docker run -p 8080:8080 orasean/ktor-server`
 5. Now Server is running

### Run Test Server
In order to run the unit test for client code, a different docker image
is required. The only difference is the image name.
From step 4:
4. Run the following two commend:
   a. Download the server
   $ `docker pull orasean/test-server`
   b. Run the server in the docker
   $ `docker run -p 8080:8080 orasean/test-server`
5. Now Server is running
### Install the Application

1. Double Click the `.dmg` installer
2. Drag the App Icon to the application folder
3. Run the App in the Launchpad or in the application folder
4. Use can use the app!
5. You should always run the Server first, then the App.

## On Windows

### Install Docker to Run Server:

 1. Go to Docker website: Download Link for [Windows](https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe?_gl=1*1klvsx3*_ga*MTE5MzU4MjAyOC4xNzAxMDQ1MDk4*_ga_XJWPQMJYHQ*MTcwMTc0MzA5My41LjEuMTcwMTc0MzA5Ny41Ni4wLjA.)
 2. Install Docker
 3. Open Docker (Ensure Docker is on)
 4. Run the following two commend:
	 a. Download the server
    $ `docker pull orasean/ktorserver-image` 
    b. Run the server in the docker
    $ `docker run -p 8080:8080 orasean/ktorserver-image`
 5. Now Server is running

### Install the Application

1. Double Click the `.msi` installer.
2. Run the installer by following the in instruction to install the app in the Windows system.
3. Run the App in the Start Menu or in the installation directory (Usually in `C:\Program Files\application\application.exe`)
4. Use can use the app!
5. You should always run the Server first, then the App.

