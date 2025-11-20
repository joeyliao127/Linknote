package com.penguin.linknote.domain.rbac;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathResolver {

    private static final Pattern NOTEBOOK_PATTERN =
            Pattern.compile("^/api/notebooks/([a-fA-F0-9\\-]{36})(/)?$");

    private static final Pattern NOTE_PATTERN =
            Pattern.compile("^/api/notebooks/([a-fA-F0-9\\-]{36})/notes/([a-fA-F0-9\\-]{36})(/)?$");

    /**
     * 主方法：解析 URL
     */
    public static ParsedResource resolve(String path) {

        // 1. 先匹配 NOTE
        Matcher noteMatcher = NOTE_PATTERN.matcher(path);
        if (noteMatcher.matches()) {
            UUID noteId = UUID.fromString(noteMatcher.group(2));
            return new ParsedResource(ResourceType.NOTE, noteId);
        }

        // 2. 匹配 NOTEBOOK
        Matcher notebookMatcher = NOTEBOOK_PATTERN.matcher(path);
        if (notebookMatcher.matches()) {
            UUID notebookId = UUID.fromString(notebookMatcher.group(1));
            return new ParsedResource(ResourceType.NOTEBOOK, notebookId);
        }

        // 3. 找不到代表此 path 不受 ACL 管理 → return null（授權階段直接 allow）
        return null;
    }
}