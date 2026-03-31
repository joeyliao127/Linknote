import type { Pagination } from "~~/types";

export interface InvitationDTO {
    id: string;
    inviteeName?: string;
    inviteeEmail?: string;
    inviterName?: string;
    notebookId?: string;
    notebookTitle?: string;
    message?: string;
    status: "pending" | "accepted" | "rejected" | "resend";
}

export interface InvitationCreateCommand {
    inviteeEmail: string;
    notebookId: string;
    message?: string;
}

export interface InvitationUpdateCommand {
    invitationId: string;
    status: "accept" | "reject" | "resend";
}

export const useInvitation = () => {
    const indexReceived = (query?: { page?: number; pageSize?: number }) =>
        $fetch<Pagination<InvitationDTO>>("/api/invitations", { query });

    const indexSent = (query?: { page?: number; pageSize?: number }) =>
        $fetch<Pagination<InvitationDTO>>("/api/invitations/sent", { query });

    const createInvitation = (command: InvitationCreateCommand) =>
        $fetch<InvitationDTO>("/api/invitations", {
            method: "POST",
            body: command,
        });

    const updateInvitation = (command: InvitationUpdateCommand) =>
        $fetch<InvitationDTO>("/api/invitations", {
            method: "PUT",
            body: command,
        });

    const deleteInvitation = (invitationId: string) =>
        $fetch("/api/invitations", {
            method: "DELETE",
            query: { invitationId },
        });

    return { indexReceived, indexSent, createInvitation, updateInvitation, deleteInvitation };
};
