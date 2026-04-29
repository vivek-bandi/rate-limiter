# Rate Limiter Project

A comprehensive Spring Boot implementation demonstrating two popular rate-limiting algorithms: **Fixed Window** and **Token Bucket**. This project serves as both a reference implementation and a performance comparison study for rate-limiting strategies in distributed systems.

## 📋 Project Overview

Rate limiting is a critical component of API gateway design and traffic management. This project implements two industry-standard algorithms with Redis backend, allowing direct performance comparison and evaluation of their respective strengths and weaknesses.

### Included Implementations

- **Fixed Window Rate Limiter** - Traditional time-window based approach
- **Token Bucket Rate Limiter** - Token-based algorithmic approach

Both implementations use Redis for distributed state management and are fully containerized for easy deployment.

## 🏗️ Project Structure

```
rate-limiter/
├── fixed-window-rate-limiter/       # Fixed window algorithm implementation
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/vivek/ratelimiter/
│   │   │   │   ├── FixedWindowRateLimiterApplication.java
│   │   │   │   ├── controller/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       ├── application.yaml
│   │   │       └── scripts/fixed_window.lua
│   │   └── test/
│   └── pom.xml
│
├── token-bucket-rate-limiter/       # Token bucket algorithm implementation
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/vivek/ratelimiter/
│   │   │   │   ├── TokenBucketRateLimiterApplication.java
│   │   │   │   ├── controller/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       ├── application.yaml
│   │   │       └── scripts/token_bucket.lua
│   │   └── test/
│   └── pom.xml
│
├── PERFORMANCE_COMPARISON.md        # Detailed algorithm comparison and analysis
└── README.md                        # This file
```

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.8.0** or higher
- **Redis 7.0** or higher (for local testing)
- **Docker** and **Docker Compose** (optional, for containerized setup)

## 🚀 Quick Start

### Option 1: Local Development (Manual Redis)

#### 1. Start Redis Server

```bash
# macOS (using Homebrew)
brew services start redis

# Linux (Ubuntu/Debian)
sudo systemctl start redis-server

# Docker
docker run -d -p 6379:6379 redis:7-alpine
```

#### 2. Build the Projects

```bash
# Build Fixed Window Rate Limiter
cd fixed-window-rate-limiter
mvn clean install

# Build Token Bucket Rate Limiter
cd ../token-bucket-rate-limiter
mvn clean install
```

#### 3. Run Applications

**Terminal 1 - Fixed Window:**
```bash
cd fixed-window-rate-limiter
mvn spring-boot:run
```

**Terminal 2 - Token Bucket:**
```bash
cd token-bucket-rate-limiter
mvn spring-boot:run
```

Both applications will start on:
- Fixed Window: `http://localhost:8080`
- Token Bucket: `http://localhost:8081` (if configured)

### Option 2: Docker Compose (Recommended)

```bash
# Create docker-compose.yml in the root directory
docker-compose up -d

# View logs
docker-compose logs -f
```

## 📡 API Endpoints

Both implementations expose the same REST API for easy comparison:

### Check Rate Limit Status

```bash
curl "http://localhost:8080/api/check?clientId=user123"
```

**Response:**
- `ALLOWED` - Request is within rate limit
- `BLOCKED` - Request exceeds rate limit

### Examples

```bash
# Test with client ID 'user1'
curl "http://localhost:8080/api/check?clientId=user1"
# Output: ALLOWED

# Rapid successive requests
for i in {1..10}; do 
  curl "http://localhost:8080/api/check?clientId=user1"
done
```

## ⚙️ Configuration

### Fixed Window Rate Limiter

Edit `fixed-window-rate-limiter/src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: fixed-window-rate-limiter
  data:
    redis:
      host: localhost
      port: 6379

server:
  port: 8080
```

**Rate Limit Parameters** (in `RateLimiterService.java`):
- Max requests: `5`
- Window duration: `10` seconds

### Token Bucket Rate Limiter

Edit `token-bucket-rate-limiter/src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: token-bucket-rate-limiter
  data:
    redis:
      host: localhost
      port: 6379

server:
  port: 8081
```

**Rate Limit Parameters** (in `TokenBucketService.java`):
- Bucket capacity: `5` tokens
- Refill rate: `1` token/second

## 🧪 Testing

### Unit Tests

```bash
# Run tests for Fixed Window
cd fixed-window-rate-limiter
mvn test

# Run tests for Token Bucket
cd ../token-bucket-rate-limiter
mvn test
```

### Manual Load Testing

```bash
# Using Apache Bench
ab -n 100 -c 10 "http://localhost:8080/api/check?clientId=testUser"

# Using curl with loop
for i in {1..20}; do
  echo "Request $i:"
  curl "http://localhost:8080/api/check?clientId=user1"
  echo
  sleep 0.1
done
```

### Testing Different Clients

```bash
# Each client has independent rate limit
curl "http://localhost:8080/api/check?clientId=alice"
curl "http://localhost:8080/api/check?clientId=bob"
curl "http://localhost:8080/api/check?clientId=charlie"
```

## 📊 Performance Comparison

For detailed analysis of algorithm performance, behavior, fairness, and operational characteristics, see:

📖 **[PERFORMANCE_COMPARISON.md](./PERFORMANCE_COMPARISON.md)**

This comprehensive document includes:
- Algorithm complexity analysis
- Latency and throughput metrics
- Behavioral comparison under various traffic patterns
- Real-world scenario analysis
- Production deployment recommendations
- Configuration optimization guidelines

## 🔍 Implementation Details

### Fixed Window (`fixed_window.lua`)

Uses a Redis counter that resets after a fixed time window:

```lua
-- Increment counter if within limit
if current < max_requests then
    redis.call('INCR', key)
    return 1  -- ALLOWED
else
    return 0  -- BLOCKED
end
```

**Characteristics:**
- Simple and predictable
- Potential for burst at window boundaries
- Fast execution (~1-2ms)

### Token Bucket (`token_bucket.lua`)

Uses a refillable bucket of tokens consumed per request:

```lua
-- Refill tokens based on time passed
local time_passed = (current_time - last_refill) / 1000
local refill = time_passed * refill_rate
tokens = math.min(capacity, tokens + refill)

-- Consume token if available
if tokens >= 1 then
    tokens = tokens - 1
    return 1  -- ALLOWED
end
```

**Characteristics:**
- Smooth traffic distribution
- Prevents burst amplification
- Slightly higher overhead (~2-3ms)

## 📈 Monitoring and Debugging

### Check Redis State

```bash
# Connect to Redis CLI
redis-cli

# View all rate limit keys
KEYS rate_limit:*

# Check specific client state
GET rate_limit:user1
HGETALL rate_limit:user1

# Monitor real-time Redis commands
MONITOR
```

### Application Logs

```bash
# Follow logs for Fixed Window
docker-compose logs -f fixed-window

# Follow logs for Token Bucket
docker-compose logs -f token-bucket
```

## 🛠️ Troubleshooting

### Redis Connection Error

```
Error: Unable to connect to Redis at localhost:6379
```

**Solution:**
- Verify Redis is running: `redis-cli ping` (should return `PONG`)
- Check Redis host/port in `application.yaml`
- Ensure firewall allows port 6379

### Port Already in Use

```
Error: Tomcat connector failed: Port 8080 already in use
```

**Solution:**
- Change port in `application.yaml`: `server.port: 8082`
- Or kill existing process: `lsof -ti:8080 | xargs kill -9`

### Rate Limiting Too Strict

Increase limits in service class:
```java
"5",   // max_requests (or capacity)
"10"   // window/refill_rate
```

## 📚 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)
- [Redis Lua Scripting](https://redis.io/commands/eval/)
- [OWASP Rate Limiting Cheat Sheet](https://cheatsheetseries.owasp.org/)
- [RFC 6585 - HTTP 429 Status Code](https://tools.ietf.org/html/rfc6585)

## 📝 License

This project is provided as-is for educational and reference purposes.

## 🤝 Contributing

Contributions are welcome. Please ensure:
1. Code follows Spring Boot conventions
2. Tests pass: `mvn test`
3. Build succeeds: `mvn clean install`
4. Performance impact is analyzed before merging

## 📧 Support

For questions or issues, please refer to the [PERFORMANCE_COMPARISON.md](./PERFORMANCE_COMPARISON.md) for detailed algorithm analysis or check application logs for troubleshooting.

---

**Last Updated:** April 2026  
**Java Version:** 21  
**Spring Boot Version:** 4.0.6
