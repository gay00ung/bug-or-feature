#!/usr/bin/env sh
set -e

# Always run the built fat jar from a stable path, ignoring any overridden command.
exec java -jar /app/app.jar

