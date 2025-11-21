module.exports = {
  networks: {
    development: {
      host: "ganache",
      port: 8545,
      network_id: "1337",
      gas: 6721975,
      gasPrice: 20000000000
    }
  },
  
  contracts_directory: './contracts',
  contracts_build_directory: './build/contracts',
  
  compilers: {
    solc: {
      version: "0.8.0",
      settings: {
        optimizer: {
          enabled: true,
          runs: 200
        }
      }
    }
  }
};
