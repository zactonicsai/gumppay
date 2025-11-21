@echo off
REM CrossPay Blockchain - Build and Run Script for Windows

echo ================================
echo CrossPay Blockchain Setup
echo ================================
echo.

REM Check if Java is installed
echo Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found. Please install Java JDK 11 or higher.
    pause
    exit /b 1
)
echo [OK] Java found

javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java compiler not found. Please install Java JDK.
    pause
    exit /b 1
)
echo [OK] Java compiler found

echo.
echo Downloading JSON library...

REM Download JSON library if not present
if not exist json-20230227.jar (
    echo Downloading org.json library...
    curl -L -o json-20230227.jar https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to download JSON library
        echo Please manually download from:
        echo https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar
        pause
        exit /b 1
    )
    echo [OK] JSON library downloaded
) else (
    echo [OK] JSON library already present
)

echo.
echo Compiling Java files...

REM Compile Java files
javac -cp json-20230227.jar;. Block.java Transaction.java Blockchain.java BlockchainServer.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed
    pause
    exit /b 1
)
echo [OK] Compilation successful

echo.
echo ================================
echo Setup Complete!
echo ================================
echo.
echo To start the server, run:
echo   java -cp json-20230227.jar;. BlockchainServer
echo.
echo Then open index.html in your browser or navigate to:
echo   http://localhost:8080
echo.
echo Default accounts:
echo   - alice@email.com (Balance: $10,000)
echo   - bob@email.com (Balance: $5,000)
echo.

REM Ask if user wants to start the server
set /p start="Start the server now? (y/n) "
if /i "%start%"=="y" (
    echo.
    echo Starting CrossPay Blockchain Server...
    echo Press Ctrl+C to stop the server
    echo.
    java -cp json-20230227.jar;. BlockchainServer
)

pause
