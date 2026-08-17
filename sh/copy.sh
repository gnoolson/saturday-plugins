#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"

PLUGINS_FOLDER=/data/projects/java/saturday/plugins
ITEMS="cache db http-client json locker now telegram-bot timer serial"

for ITEM in $ITEMS; do
    cd ../$ITEM/target
    cp -f $ITEM-plugin.jar $PLUGINS_FOLDER/$ITEM/$ITEM-plugin.jar
    cd ..
done
