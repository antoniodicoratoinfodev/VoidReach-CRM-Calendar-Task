package com.crm.service;

import com.crm.model.CrmDataSnapshot;
import com.crm.model.UserAccount;
import com.crm.repository.CrmBackupService;
import com.crm.repository.CrmDataRepository;
import com.crm.repository.LocalCrmDataRepository;

import java.util.Objects;

/** Coordinates per-account CRM persistence and rotating backups. */
public final class CrmWorkspaceService implements AutoCloseable {
    private final CrmDataRepository repository;
    private final CrmBackupService backupService;
    private UserAccount currentUser;

    public CrmWorkspaceService() {
        this(new LocalCrmDataRepository(), new CrmBackupService());
    }

    CrmWorkspaceService(CrmDataRepository repository, CrmBackupService backupService) {
        this.repository = Objects.requireNonNull(repository);
        this.backupService = Objects.requireNonNull(backupService);
    }

    public CrmDataSnapshot open(UserAccount user) {
        close();
        currentUser = Objects.requireNonNull(user);
        try {
            return repository.loadForUser(user.getId());
        } finally {
            backupService.start(user.getId());
        }
    }

    public void save(CrmDataSnapshot snapshot) {
        if (currentUser == null) return;
        repository.saveForUser(currentUser.getId(), Objects.requireNonNull(snapshot));
    }

    @Override
    public void close() {
        backupService.close();
        currentUser = null;
    }
}
