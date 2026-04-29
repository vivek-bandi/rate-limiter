local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2]) -- tokens per second
local current_time = tonumber(ARGV[3])

-- fetch existing state
local data = redis.call('HMGET', key, 'tokens', 'last_refill')

local tokens = tonumber(data[1])
local last_refill = tonumber(data[2])

-- initialize if not present
if tokens == nil then
    tokens = capacity
    last_refill = current_time
end

-- calculate time passed (in seconds)
local time_passed = (current_time - last_refill) / 1000

-- refill tokens
local refill = time_passed * refill_rate
tokens = math.min(capacity, tokens + refill)

local allowed = 0

if tokens >= 1 then
    tokens = tokens - 1
    allowed = 1
end

-- save updated values
redis.call('HMSET', key,
    'tokens', tokens,
    'last_refill', current_time
)

-- expire key to avoid memory leak
redis.call('EXPIRE', key, 60)

return allowed