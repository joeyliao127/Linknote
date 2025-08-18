package com.penguin.linknote.service.impl;

import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.entity.Notebook;
import com.penguin.linknote.entity.QNotebook;
import com.penguin.linknote.repository.NotebookRepository;
import com.penguin.linknote.service.NotebookService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotebookServiceImpl implements NotebookService {

    private final NotebookRepository notebookRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public NotebookServiceImpl (NotebookRepository notebookRepository, JPAQueryFactory jpaQueryFactory) {
        this.notebookRepository = notebookRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<NotebookDTO> indexNotebooks(UUID userId, String title, Boolean active) {
        QNotebook qNotebook = QNotebook.notebook;

        JPAQuery<Notebook> notebookJPAQuery = jpaQueryFactory.selectFrom(qNotebook).where(
                userId != null ? qNotebook.userId.eq(userId) : null,
                title != null ? qNotebook.title.eq(title) : null,
                active != null ? qNotebook.isActive.eq(active) : null
        );

        List<Notebook> notebooks = notebookJPAQuery.fetch();

        notebookRepository.findAll();

        return NotebookDTO.fromEntityList(notebooks);
    }


    @Override
    public NotebookDTO getNotebookById(UUID notebookId) {
        return null;
    }

    @Override
    public NotebookDTO createNotebook(NotebookCommand notebookCommand, UUID userId) {
        Notebook notebook = new Notebook();
        notebook.setId(generateUUID());
        notebook.setTitle(notebookCommand.getTitle());
        notebook.setDescription(notebookCommand.getDescription());
        notebook.setIsActive(true);
        notebook.setCreatedAt(Instant.now());
        notebook.setUpdatedAt(Instant.now());
        notebook.setUserId(userId);

        return  NotebookDTO.fromEntity(notebookRepository.save(notebook));
    }

    @Override
    public NotebookDTO updateNotebook(UUID notebookId, NotebookCommand notebookCommand) {
        Notebook existingNotebook = notebookRepository.findById(notebookId).orElseThrow(()-> new EntityNotFoundException("Notebook not found"));

        Notebook notebook = new Notebook();
        notebook.setId(notebookId);
        notebook.setTitle(notebookCommand.getTitle() == null ? existingNotebook.getTitle() : notebookCommand.getTitle());
        notebook.setDescription(notebookCommand.getDescription() == null ? existingNotebook.getDescription() : notebookCommand.getDescription());
        notebook.setIsActive(notebookCommand.getActive() == null ? existingNotebook.getIsActive() : notebookCommand.getActive());
        notebook.setUserId(existingNotebook.getUserId());
        notebook.setUpdatedAt(Instant.now());

        return NotebookDTO.fromEntity(notebookRepository.save(notebook));
    }

    @Override
    public void deleteNotebook(UUID notebookId) {
        notebookRepository.deleteById(notebookId);
    }

    private UUID generateUUID() {
        return UUID.randomUUID();
    }
}
