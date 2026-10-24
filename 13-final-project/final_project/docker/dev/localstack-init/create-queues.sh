#!/usr/bin/env bash
set -euo pipefail

IFS=',' read -r -a QUEUE_NAMES <<< "${SQS_QUEUE_NAMES:-input-queue,dlq-queue}"

ensure_queue() {
  local queue_name="$1"
  local sanitized="${queue_name//[[:space:]]/}"

  if [[ -z "$sanitized" ]]; then
    return
  fi

  echo "Ensuring SQS queue '${sanitized}' exists..."

  if [[ "$sanitized" == *.fifo ]]; then
    if awslocal sqs create-queue \
      --queue-name "$sanitized" \
      --attributes FifoQueue=true,ContentBasedDeduplication=true >/dev/null 2>&1; then
      echo "Queue '${sanitized}' created."
      return
    fi
  else
    if awslocal sqs create-queue --queue-name "$sanitized" >/dev/null 2>&1; then
      echo "Queue '${sanitized}' created."
      return
    fi
  fi

  if awslocal sqs get-queue-url --queue-name "$sanitized" >/dev/null 2>&1; then
    echo "Queue '${sanitized}' already present."
  else
    echo "Failed to ensure queue '${sanitized}'." >&2
    exit 1
  fi
}

for queue in "${QUEUE_NAMES[@]}"; do
  ensure_queue "$queue"
done