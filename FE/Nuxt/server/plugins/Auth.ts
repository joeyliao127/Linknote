export default defineNitroPlugin((nitroApp) => {
    // 建立一個符合 Storage 介面的物件
    if (!global.sessionStorage) {
        const storage = new Map<string, string>();

        // 需實作 sessionStorage 介面 methods，否則 ts 會報錯
        global.sessionStorage = {
            getItem(key: string): string | null {
                return storage.get(key) ?? null;
            },
            setItem(key: string, value: string): void {
                storage.set(key, value);
            },
            removeItem(key: string): void {
                storage.delete(key);
            },
            clear(): void {
                storage.clear();
            },
            key(index: number): string | null {
                const keys = Array.from(storage.keys());
                return keys[index] ?? null;
            },
            get length(): number {
                return storage.size;
            },
        };
        console.log("Initialized global.sessionStorage");
        console.log(global.sessionStorage);
    }
});
