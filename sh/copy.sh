#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"

PLUGINS_FOLDER=/data/project/java/saturday/plugins
SELECTED_PLUGINS="cache db http-client json locker now timer serial event-bus"

for SELECTED_PLUGIN in $SELECTED_PLUGINS; do
    cd ../"$SELECTED_PLUGIN"/target
    cp -f "$SELECTED_PLUGIN"-plugin.jar $PLUGINS_FOLDER/"$SELECTED_PLUGIN"/"$SELECTED_PLUGIN"-plugin.jar
    cd ..
done
