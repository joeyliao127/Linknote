package com.penguin.linknote.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.notebook.NotebookCondition;
import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.domain.notebook.exception.InvalidNotebookParameterException;
import com.penguin.linknote.domain.notebook.exception.NotebookNotFoundException;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.domain.rbac.RoleType;
import com.penguin.linknote.entity.Notebook;
import com.penguin.linknote.repository.NotebookRepository;
import com.penguin.linknote.repository.UserRepository;
import com.penguin.linknote.service.NotebookService;
import com.penguin.linknote.service.PermissionService;

@Service
public class NotebookServiceImpl implements NotebookService {

    private final NotebookRepository notebookRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    public NotebookServiceImpl(
            NotebookRepository notebookRepository,
            UserRepository userRepository,
            PermissionService permissionService)
    {
        this.notebookRepository = notebookRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    @Override
    public PageResponse<NotebookDTO> index(UUID userId, NotebookCondition condition, PageCommand pageCommand) {
        int page = pageCommand == null || pageCommand.getPage() == null ? 1 : pageCommand.getPage();
        int limit = pageCommand == null || pageCommand.getPageSize() == null ? 0 : pageCommand.getPageSize();

        if (condition == null) {
            condition = new NotebookCondition();
        }
        condition.setUserId(userId);
        PageResponse<Notebook> notebookPage = notebookRepository.paginate(page, limit, condition);

        PageResponse<NotebookDTO> response = new PageResponse<>();
        response.setCount(notebookPage.getCount());
        response.setCurrentPage(notebookPage.getCurrentPage());
        response.setPageSize(notebookPage.getPageSize());
        response.setTotalPage(notebookPage.getTotalPage());
        response.setItems(NotebookDTO.fromEntityList(notebookPage.getItems()));
        return response;
    }


    @Override
    public NotebookDTO get(UUID notebookId) {
        Notebook notebook = notebookRepository.get(notebookId)
                .orElseThrow(NotebookNotFoundException::new);
        return NotebookDTO.fromEntity(notebook);
    }

    @Override
    public NotebookDTO create(NotebookCommand notebookCommand, UUID userId) {
        userRepository.get(userId).orElseThrow(InvalidNotebookParameterException::new);
        Notebook notebook = new Notebook();
        notebook.setId(UUID.randomUUID());
        notebook.setTitle(notebookCommand.getTitle());
        notebook.setDescription(notebookCommand.getDescription());
        notebook.setIsActive(true);
        notebook.setCreatedAt(Instant.now());
        notebook.setUpdatedAt(Instant.now());
        Notebook savedNotebook = notebookRepository.create(notebook);
        permissionService.addResourceRolePermission(userId, savedNotebook.getId(), RoleType.ROLE_OWNER, ResourceType.NOTEBOOK);
        return NotebookDTO.fromEntity(savedNotebook);
    }

    @Override
    public NotebookDTO update(UUID notebookId, NotebookCommand notebookCommand) {
        Notebook existingNotebook = notebookRepository.get(notebookId)
                .orElseThrow(NotebookNotFoundException::new);
        existingNotebook.setId(notebookId);
        existingNotebook.setTitle(notebookCommand.getTitle() == null ? existingNotebook.getTitle() : notebookCommand.getTitle());
        existingNotebook.setDescription(notebookCommand.getDescription() == null ? existingNotebook.getDescription() : notebookCommand.getDescription());
        existingNotebook.setIsActive(notebookCommand.getActive() == null ? existingNotebook.getIsActive() : notebookCommand.getActive());
        existingNotebook.setUpdatedAt(Instant.now());

        return NotebookDTO.fromEntity(notebookRepository.update(existingNotebook));
    }

    @Override
    public void delete(UUID notebookId) {
        notebookRepository.delete(notebookId);
    }
}
