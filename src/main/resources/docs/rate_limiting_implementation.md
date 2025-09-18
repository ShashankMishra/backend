# Rate Limiting Implementation

This document provides a detailed explanation of the rate limiting implementation in the application.

## Overview

The rate limiting feature is implemented using annotations and interceptors. This allows for a declarative approach to applying rate limits to controller methods.

There are two types of rate limiting available:

*   **Rate Limiting by User:** Limits the number of requests a user can make to a specific endpoint within a given time window.
*   **Rate Limiting by IP Address:** Limits the number of requests an IP address can make to a specific endpoint within a given time window.

## Annotations

Two annotations are provided to enable rate limiting on controller methods:

### `@RateLimitByUser`

This annotation applies rate limiting based on the current user's ID.

**Parameters:**

*   `windowSize`: The time window in seconds. Defaults to `60`.
*   `maxRequests`: The maximum number of requests allowed in the time window. Defaults to `10`.

**Example:**

```java
@RateLimitByUser(windowSize = 120, maxRequests = 20)
```

### `@RateLimitByIp`

This annotation applies rate limiting based on the client's IP address.

**Parameters:**

*   `windowSize`: The time window in seconds. Defaults to `60`.
*   `maxRequests`: The maximum number of requests allowed in the time window. Defaults to `20`.

**Example:**

```java
@RateLimitByIp(windowSize = 120, maxRequests = 40)
```

## Interceptors

The interceptors contain the logic for enforcing the rate limits defined by the annotations.

### `RateLimitByUserInterceptor`

This interceptor handles the `@RateLimitByUser` annotation. It performs the following steps:

1.  Retrieves the `windowSize` and `maxRequests` values from the annotation.
2.  Gets the current user's ID from the `UserService`.
3.  Constructs a unique Redis key using the user ID and the method name.
4.  Uses a Redis sorted set to store the timestamps of the user's requests.
5.  Removes any timestamps that are older than the `windowSize`.
6.  Counts the number of requests in the current time window.
7.  If the number of requests exceeds `maxRequests`, it throws a `LimitReachedException`.
8.  Otherwise, it adds the current timestamp to the sorted set and allows the request to proceed.

### `RateLimitByIpInterceptor`

This interceptor handles the `@RateLimitByIp` annotation. It performs the following steps:

1.  Retrieves the `windowSize` and `maxRequests` values from the annotation.
2.  Gets the client's IP address from the `X-Forwarded-For` header.
3.  Constructs a unique Redis key using the IP address and the method name.
4.  Uses a Redis sorted set to store the timestamps of the IP address's requests.
5.  Removes any timestamps that are older than the `windowSize`.
6.  Counts the number of requests in the current time window.
7.  If the number of requests exceeds `maxRequests`, it throws a `LimitReachedException`.
8.  Otherwise, it adds the current timestamp to the sorted set and allows the request to proceed.

## Usage

To apply rate limiting to a controller method, simply add the desired annotation to the method.

**Example:**

```java
import com.qrust.common.interceptor.RateLimitByIp;
import com.qrust.common.interceptor.RateLimitByUser;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RateLimitByUser(windowSize = 60, maxRequests = 10)
    @RateLimitByIp(windowSize = 60, maxRequests = 20)
    public String hello() {
        return "Hello from Quarkus REST";
    }
}
```

## Redis Data Structure

The rate limiting implementation uses a Redis sorted set to store the timestamps of requests. The key of the sorted set is a combination of the rate limiting type (user or IP), the user ID or IP address, and the method name.

The value of each element in the sorted set is the timestamp of the request.

This data structure allows for efficient querying of requests within a specific time window.
