## Redis-based Queue Implementation for Asynchronous WhatsApp Messaging

This document provides a detailed explanation of the Redis-based queueing system implemented to send WhatsApp messages asynchronously upon a QR code scan.

### High-Level Overview

The primary goal was to replace the existing `CompletableFuture.runAsync` implementation in the `ScanController` with a more robust and reliable Redis-based queueing system. This new system ensures guaranteed message delivery with features like a 15-second initial delay, retries, and a dead-letter queue (DLQ) for messages that fail to be processed.

### Corrected Flow

1.  **`ScanController`**: Enqueues the message to the `whatsapp_messages_scheduled` sorted set with a score of `now + 15 seconds`.
2.  **`moveScheduledMessages`**: This method runs periodically. It checks the `whatsapp_messages_scheduled` sorted set and moves any messages with a score less than or equal to the current time to the `whatsapp_messages` list.
3.  **`consumeMessage`**: This method consumes messages from the `whatsapp_messages` list and processes them in a reliable way using `RPOPLPUSH`.

### `RedisQueueService`

A new service, `RedisQueueService`, was created to abstract the process of enqueuing messages into Redis. This service is located at `src/main/java/com/qrust/common/queue/RedisQueueService.java`.

The service has a single method, `enqueue`, which takes a queue name and a message as input. It uses the `zadd` command to add the message to a Redis sorted set with a score of the current time plus 15 seconds. This sorted set acts as our scheduled queue.

### `ScanController` Changes

The `ScanController` at `src/main/java/com/qrust/user/api/ScanController.java` was modified to use the new `RedisQueueService`.

1.  **Injection**: The `RedisQueueService` is injected into the `ScanController`.
2.  **Message Creation**: A new inner class, `ScanMessage`, was created to represent the message payload. It contains the `QRCode` object, the `scanId`, and a `retryCount`.
3.  **Enqueuing**: The `CompletableFuture.runAsync` call was replaced with a call to `redisQueueService.enqueue`. The `ScanMessage` object is serialized to a JSON string before being enqueued to the `whatsapp_messages_scheduled` queue.

### `WhatsappMessageConsumer`

This is the core component of the new system, responsible for consuming and processing messages from the Redis queue. It is located at `src/main/java/com/qrust/user/service/WhatsappMessageConsumer.java`.

The service uses three scheduled methods:

1.  **`moveScheduledMessages()`**: This method runs every 5 seconds. It checks a Redis sorted set named `whatsapp_messages_scheduled` for any messages that are scheduled to be processed. The score of the sorted set is a Unix timestamp. If the current time is greater than or equal to the score, the message is moved to the main `whatsapp_messages` queue (which is a Redis list) for processing.

2.  **`consumeMessage()`**: This method runs every 10 seconds. It uses the `RPOPLPUSH` command to atomically pop a message from the `whatsapp_messages` queue and push it to a `whatsapp_messages_processing` queue. The message is then processed. If processing is successful, the message is removed from the `whatsapp_messages_processing` queue. If processing fails, the `handleFailedMessage` method is called, and the message is still removed from the `whatsapp_messages_processing` queue.

3.  **`recoverStaleMessages()`**: This method runs every hour. It checks the `whatsapp_messages_processing` queue for any messages that might have been left there due to an application crash during processing. If any messages are found, they are moved back to the `whatsapp_messages` queue to be re-processed.

#### Delay, Retries, and Dead-Letter Queue

-   **15-Second Delay**: When a message needs to be retried, it is not immediately re-enqueued. Instead, it is added to the `whatsapp_messages_scheduled` sorted set with a score equal to the current time plus 15 seconds. This ensures that there is a delay before the message is processed again.

-   **Retries**: If processing a message fails, the `handleFailedMessage` method is called. This method deserializes the message, increments the `retryCount`, and re-enqueues the message to the `whatsapp_messages_scheduled` sorted set with the delay. This is repeated up to `MAX_RETRIES` (currently set to 3).

-   **Dead-Letter Queue**: If a message fails to be processed after `MAX_RETRIES` attempts, it is moved to the `whatsapp_messages_dlq` list. This is our dead-letter queue, which can be monitored for messages that require manual investigation.

### `ScanMessage`

The `ScanMessage` class is a simple static inner class in `ScanController`. It holds the data required for processing a WhatsApp message:

-   `qrCode`: The `QRCode` object.
-   `scanId`: The ID of the scan.
-   `retryCount`: The number of times the message has been retried.

### Redis Data Structures

The following Redis keys are used:

-   `whatsapp_messages`: A Redis **list** that serves as the main message queue.
-   `whatsapp_messages_processing`: A Redis **list** that holds messages that are currently being processed.
-   `whatsapp_messages_scheduled`: A Redis **sorted set** that holds messages scheduled for future processing. The score is the timestamp when the message should be processed.
-   `whatsapp_messages_dlq`: A Redis **list** that serves as the dead-letter queue for messages that have failed processing multiple times.
