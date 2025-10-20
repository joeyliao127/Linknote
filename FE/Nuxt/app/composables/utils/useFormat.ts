import type { SelectItem } from "@nuxt/ui";

export function toSelection(
    items: any[],
    labelKey: string,
    valueKey: string
): SelectItem[] {
    return items.map((item) => {
        return {
            label: item[labelKey],
            value: item[valueKey],
        };
    });
}
