import { useRuntimeConfig } from "#imports";
import type { Pagination } from "~/types";
import type { Tag, CreateTagDTO, UpdateTagDTO } from "~/types/Tag";

const _useTag = () => {
    const runtimeConfig = useRuntimeConfig();
    // const { isPending, isError, data, error } = useQuery({
    //     queryKey: ["todos"],
    //     queryFn: async () => {
    //         const response = await fetch("/api/xxx");

    //         return response.data as ApiResult<{
    //             id: string;
    //             name: string;
    //         }>;
    //     },
    // });

    const indexTags = async (userId: string): Promise<Pagination<Tag>> => {
        const response: Pagination<Tag> = await $fetch(
            `${runtimeConfig.public.API_URL}/tags`,
            {
                method: "GET",
                headers: {
                    Authorization: userId,
                },
            }
        );

        return response;
    };

    const createTag = async (userId: string, tag: CreateTagDTO) => {
        const response: Pagination<Tag> = await $fetch(
            `${runtimeConfig.public.API_URL}/Tags`,
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
            `${runtimeConfig.public.API_URL}/Tags`,
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
            `${runtimeConfig.public.API_URL}/Tags`,
            {
                method: "DELETE",
                headers: {
                    Authorization: userId,
                },
                query: {
                    TagId: tagId,
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
