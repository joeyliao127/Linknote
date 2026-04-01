import redisDriver from "unstorage/drivers/redis";

export default defineNitroPlugin(() => {
    const storage = useStorage();

    const driver = redisDriver({
        host: process.env.REDIS_HOST || "redis",
        port: Number(process.env.REDIS_PORT) || 6379,
        db: Number(process.env.REDIS_DB) || 0,
        username: process.env.REDIS_USERNAME || undefined,
        password: process.env.REDIS_PASSWORD || undefined,
    });

    storage.mount("redis", driver);
});
