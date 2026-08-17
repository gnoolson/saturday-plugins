#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"

cd ../cache
mvn clean install
cd ../db
mvn clean install
cd ../http-client
mvn clean install
cd ../json
mvn clean install
cd ../locker
mvn clean install
cd ../now
mvn clean install
cd ../telegram-bot
mvn clean install
cd ../timer
mvn clean install
cd ../serial
mvn clean install
cd ..
