local key = KEYS[1]
local max_requests = tonumber(ARGV[1])
local window = tonumber(ARGV[2])

local current = tonumber(redis.call('GET', key) or "0")

if current == 0 then
    redis.call('SET', key, 1)
    redis.call('EXPIRE', key, window)
    return 1
elseif current < max_requests then
    redis.call('INCR', key)
    return 1
else
    return 0
end