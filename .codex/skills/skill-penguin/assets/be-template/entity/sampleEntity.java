package com.penguin.linknote.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

// TODO: 確認 table name 與欄位
@Data
public class !{upper} {

    // TODO: 若使用 auto increment，改為對應型別與建立方式
    private UUID id;

    private Instant createdAt;

    private Instant updatedAt;


    // TODO: 下方為 relation sample，記得移除

    // @Column(name = "inviter_id")
    // private UUID inviterId;

    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "invitee_id")
    // private User invitee;

    // @Column(name = "message")
    // private String message;

    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "invitation_status_id")
    // private InvitationStatusCode invitationStatusCode;

}
