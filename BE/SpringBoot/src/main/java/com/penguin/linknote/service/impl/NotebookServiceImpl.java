package com.penguin.linknote.service.impl;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.common.service.PaginationService;
import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.entity.Notebook;
import com.penguin.linknote.entity.QNotebook;
import com.penguin.linknote.entity.User;
import com.penguin.linknote.repository.NotebookRepository;
import com.penguin.linknote.repository.UserRepository;
import com.penguin.linknote.service.NotebookService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class NotebookServiceImpl implements NotebookService {

    private final NotebookRepository notebookRepository;
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final PaginationService paginationService;

    @Autowired
    public NotebookServiceImpl(
            NotebookRepository notebookRepository,
            UserRepository userRepository,
            JPAQueryFactory jpaQueryFactory,
            PaginationService paginationService)
    {
        this.notebookRepository = notebookRepository;
        this.userRepository = userRepository;
        this.jpaQueryFactory = jpaQueryFactory;
        this.paginationService = paginationService;
    }

    @Override
    public PageResponse<NotebookDTO> indexNotebooks(UUID userId, String title, Boolean active, PageCommand pageCommand) {
        QNotebook qNotebook = QNotebook.notebook;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(userId != null) booleanBuilder.and(qNotebook.user.id.eq(userId));
        if(title != null) booleanBuilder.and(qNotebook.title.eq(title));
        if(active != null) booleanBuilder.and(qNotebook.isActive.eq(active));

        JPAQuery<Notebook> notebookJPAQuery = jpaQueryFactory
                .selectFrom(qNotebook)
                .where(booleanBuilder)
                .orderBy(qNotebook.title.asc());

        return paginationService.applyPagination(notebookJPAQuery, pageCommand, NotebookDTO::fromEntity);
    }


    @Override
    public NotebookDTO getNotebookById(UUID notebookId) {
        return null;
    }

    @Override
    public NotebookDTO createNotebook(NotebookCommand notebookCommand, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        Notebook notebook = new Notebook();
        notebook.setId(UUID.randomUUID());
        notebook.setTitle(notebookCommand.getTitle());
        notebook.setDescription(notebookCommand.getDescription());
        notebook.setIsActive(true);
        notebook.setUser(user);
        notebook.setCreatedAt(Instant.now());
        notebook.setUpdatedAt(Instant.now());

        return NotebookDTO.fromEntity(notebookRepository.save(notebook));
    }

    @Override
    public NotebookDTO updateNotebook(UUID notebookId, NotebookCommand notebookCommand) {
        Notebook existingNotebook = notebookRepository.findById(notebookId).orElseThrow(() -> new EntityNotFoundException("Notebook not found"));
        existingNotebook.setId(notebookId);
        existingNotebook.setTitle(notebookCommand.getTitle() == null ? existingNotebook.getTitle() : notebookCommand.getTitle());
        existingNotebook.setDescription(notebookCommand.getDescription() == null ? existingNotebook.getDescription() : notebookCommand.getDescription());
        existingNotebook.setIsActive(notebookCommand.getActive() == null ? existingNotebook.getIsActive() : notebookCommand.getActive());
        existingNotebook.setUpdatedAt(Instant.now());

        return NotebookDTO.fromEntity(notebookRepository.save(existingNotebook));
    }

    @Override
    public void deleteNotebook(UUID notebookId) {
        notebookRepository.deleteById(notebookId);
    }
}
