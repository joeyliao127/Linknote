import { useRuntimeConfig } from "#imports";
import { toSelection } from "../utils/useFormat";
import type { Pagination } from "~/types";
import type { Tag, CreateTagDTO, UpdateTagDTO } from "~/types/Tag";
import type { SelectItem } from "@nuxt/ui";

const _useTag = () => {
    const runtimeConfig = useRuntimeConfig();

    const indexTags = async (
        userId: string
    ): Promise<Pagination<SelectItem>> => {
        const response: Pagination<Tag> = await $fetch(
            `${runtimeConfig.public.API_URL}/tags`,
            {
                method: "GET",
                headers: {
                    Authorization: userId,
                },
            }
        );

        const tags: Pagination<SelectItem> = response;
        tags.items = toSelection(response.items, "title", "id");

        return tags;
    };

    const createTag = async (userId: string, tag: CreateTagDTO) => {
        const response: Pagination<Tag> = await $fetch(
            `${runtimeConfig.public.API_URL}/tags`,
            {
                method: "POST",
                headers: {
                    Authorization: userId,
                },
                body: tag,
            }
        );

        return response;
    };

    const updateTag = async (userId: string, tag: UpdateTagDTO) => {
        const response: Pagination<Tag> = await $fetch(
            `${runtimeConfig.public.API_URL}/tags/${tag.id}`,
            {
                method: "PUT",
                headers: {
                    Authorization: userId,
                },
                body: tag,
            }
        );

        return response;
    };

    const deleteTag = async (userId: string, tagId: string) => {
        const response: Pagination<Tag> = await $fetch(
            `${runtimeConfig.public.API_URL}/tags`,
            {
                method: "DELETE",
                headers: {
                    Authorization: userId,
                },
                query: {
                    tagId: tagId,
                },
            }
        );
    };

    return {
        indexTags,
        createTag,
        updateTag,
        deleteTag,
    };
};

export const useTag = _useTag;
