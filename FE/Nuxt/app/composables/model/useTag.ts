import { useRuntimeConfig } from "#imports";
import { toSelection } from "../utils/useFormat";
import type { Pagination } from "~~/types";
import type { Tag, CreateTagDTO, UpdateTagDTO } from "~~/types/Tag";
import type { SelectItem } from "@nuxt/ui";

const _useTag = () => {
    const runtimeConfig = useRuntimeConfig();
    const baseURL = `${runtimeConfig.public.API_URL}/tags`;

    const indexTags = async (): Promise<Pagination<SelectItem>> => {
        const response: Pagination<Tag> = await $fetch(baseURL, {
            method: "GET",
        });

        const tags: Pagination<SelectItem> = response;
        tags.items = toSelection(response.items, "title", "id");

        return tags;
    };

    const createTag = async (tag: CreateTagDTO) => {
        const response: Pagination<Tag> = await $fetch(baseURL, {
            method: "POST",
            body: tag,
        });

        return response;
    };

    const updateTag = async (tag: UpdateTagDTO) => {
        const response: Pagination<Tag> = await $fetch(`${baseURL}/${tag.id}`, {
            method: "PUT",
            body: tag,
        });

        return response;
    };

    const deleteTag = async (tagId: string) => {
        const response: Pagination<Tag> = await $fetch(`${baseURL}/${tagId}`, {
            method: "DELETE",
        });
    };

    return {
        indexTags,
        createTag,
        updateTag,
        deleteTag,
    };
};

export const useTag = _useTag;
