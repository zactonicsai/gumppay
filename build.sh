#!/bin/bash

# CrossPay Blockchain - Build and Run Script

echo "================================"
echo "CrossPay Blockchain Setup"
echo "================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Java is installed
echo -e "${YELLOW}Checking Java installation...${NC}"
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo -e "${GREEN}✓ Java found: $java_version${NC}"
else
    echo -e "${RED}✗ Java not found. Please install Java JDK 11 or higher.${NC}"
    exit 1
fi

# Check if javac is installed
if command -v javac &> /dev/null; then
    echo -e "${GREEN}✓ Java compiler found${NC}"
else
    echo -e "${RED}✗ Java compiler not found. Please install Java JDK.${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}Downloading JSON library...${NC}"

# Download JSON library if not present
if [ ! -f "json-20230227.jar" ]; then
    echo "Downloading org.json library..."
    curl -L -o json-20230227.jar https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ JSON library downloaded${NC}"
    else
        echo -e "${RED}✗ Failed to download JSON library${NC}"
        echo "Please manually download from:"
        echo "https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar"
        exit 1
    fi
else
    echo -e "${GREEN}✓ JSON library already present${NC}"
fi

echo ""
echo -e "${YELLOW}Compiling Java files...${NC}"

# Compile Java files
javac -cp json-20230227.jar:. Block.java Transaction.java Blockchain.java BlockchainServer.java

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi

echo ""
echo "================================"
echo -e "${GREEN}Setup Complete!${NC}"
echo "================================"
echo ""
echo "To start the server, run:"
echo -e "${YELLOW}  java -cp json-20230227.jar:. BlockchainServer${NC}"
echo ""
echo "Then open index.html in your browser or navigate to:"
echo -e "${YELLOW}  http://localhost:8080${NC}"
echo ""
echo "Default accounts:"
echo "  • alice@email.com (Balance: $10,000)"
echo "  • bob@email.com (Balance: $5,000)"
echo ""

# Ask if user wants to start the server
read -p "Start the server now? (y/n) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo -e "${GREEN}Starting CrossPay Blockchain Server...${NC}"
    echo "Press Ctrl+C to stop the server"
    echo ""
    java -cp json-20230227.jar:. BlockchainServer
fi
