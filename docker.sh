docker run \
    --volume "/tmp/some/local/path/:/" \
    ghcr.io/argotorg/solc:stable \
        /CrossBorderPayment.sol \
        --abi \
        --bin \
        --output-dir /sources/output/