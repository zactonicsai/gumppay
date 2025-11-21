#!/bin/sh

echo "╔══════════════════════════════════════════════════╗"
echo "║   CrossPay Smart Contract Deployer              ║"
echo "╚══════════════════════════════════════════════════╝"

# Wait for Ganache to be ready
echo "Waiting for Ganache to be ready..."
until wget -q -O - http://ganache:8545 > /dev/null 2>&1; do
  echo "Ganache not ready yet, waiting..."
  sleep 2
done

echo "✓ Ganache is ready!"

# Compile contracts
echo "Compiling Solidity contracts..."
truffle compile

if [ $? -eq 0 ]; then
  echo "✓ Contracts compiled successfully!"
else
  echo "✗ Contract compilation failed!"
  exit 1
fi

# Deploy contracts
echo "Deploying contracts to Ganache..."
truffle migrate --network development

if [ $? -eq 0 ]; then
  echo "✓ Contracts deployed successfully!"
  
  # Copy artifacts to shared volume
  if [ -d "/contracts" ]; then
    cp -r build/contracts/* /contracts/
    echo "✓ Contract artifacts saved!"
  fi
  
  # Display contract address
  echo ""
  echo "╔══════════════════════════════════════════════════╗"
  echo "║   Deployment Complete!                           ║"
  echo "╚══════════════════════════════════════════════════╝"
  echo ""
  echo "Contract deployed to Ganache at http://ganache:8545"
  echo "Network ID: 1337"
  echo ""
  
  # Keep container running for logs
  tail -f /dev/null
else
  echo "✗ Contract deployment failed!"
  exit 1
fi
