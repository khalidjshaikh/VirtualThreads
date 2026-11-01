#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
javac --enable-preview --release 27 -d out *.java
echo "Compiled into out/"
echo "Run: java --enable-preview -cp out <ClassName>"