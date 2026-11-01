#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
./run.sh
for cls in VirtualThreadsIntro VirtualThreadExecutors VirtualThreadPerformance VirtualThreadPinning VirtualThreadThreadLocal ScopedValuesDemo StructuredConcurrencyDemo VirtualThreadSchedulerDemo VirtualThreadLockContention VirtualThreadBatchProcessor VirtualThreadWebServer VirtualThreadCustomFactory VirtualThreadCancellation VirtualThreadCompletableFuture; do
  echo "================ $cls ================"
  java --enable-preview -cp out "$cls"
done