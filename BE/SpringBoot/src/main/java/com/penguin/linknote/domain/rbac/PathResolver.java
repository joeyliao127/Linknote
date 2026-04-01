package com.penguin.linknote.domain.rbac;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathResolver {

    private static final Pattern NOTEBOOK_PATTERN =
            Pattern.compile("^/api/notebooks/([a-fA-F0-9\\-]{36})(/)?$");

    private static final Pattern NOTE_IN_NOTEBOOK_PATTERN =
            Pattern.compile("^/api/notebooks/([a-fA-F0-9\\-]{36})/notes/([a-fA-F0-9\\-]{36})(/)?$");

    private static final Pattern NOTE_DIRECT_PATTERN =
            Pattern.compile("^/api/notes/([a-fA-F0-9\\-]{36})(/[^/]+)*(/)?$");

    /**
     * 主方法：解析 URL
     */
    public static ParsedResource resolve(String path) {

        // 1. 先匹配 /api/notebooks/{notebookId}/notes/{noteId}
        Matcher noteInNotebookMatcher = NOTE_IN_NOTEBOOK_PATTERN.matcher(path);
        if (noteInNotebookMatcher.matches()) {
            UUID noteId = UUID.fromString(noteInNotebookMatcher.group(2));
            return new ParsedResource(ResourceType.NOTE, noteId);
        }

        // 2. 匹配 /api/notes/{noteId} (direct note operations)
        Matcher noteDirectMatcher = NOTE_DIRECT_PATTERN.matcher(path);
        if (noteDirectMatcher.matches()) {
            UUID noteId = UUID.fromString(noteDirectMatcher.group(1));
            return new ParsedResource(ResourceType.NOTE, noteId);
        }

        // 3. 匹配 NOTEBOOK
        Matcher notebookMatcher = NOTEBOOK_PATTERN.matcher(path);
        if (notebookMatcher.matches()) {
            UUID notebookId = UUID.fromString(notebookMatcher.group(1));
            return new ParsedResource(ResourceType.NOTEBOOK, notebookId);
        }

        // 4. 找不到代表此 path 不受 ACL 管理 → return null（授權階段直接 allow）
        return null;
    }
}