package com.crm.service;

import com.crm.model.UserAccount;
import com.crm.repository.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/** Stores only the selected account email, never a password or password hash. */
public class SessionService {
    private final UserRepository users;
    private final Path file = Path.of(System.getProperty("user.home"), ".voidreach-crm", "session.properties");
    public SessionService(UserRepository users) { this.users = users; }

    public Optional<UserAccount> getRememberedUser() {
        if (!Files.exists(file)) return Optional.empty();
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(file)) { properties.load(in); }
        catch (IOException e) { return Optional.empty(); }
        return Optional.ofNullable(properties.getProperty("email")).flatMap(users::findByEmail);
    }
    public void remember(UserAccount user) {
        Properties properties = new Properties(); properties.setProperty("email", user.getEmail());
        try { Files.createDirectories(file.getParent()); try (OutputStream out = Files.newOutputStream(file)) { properties.store(out, "VoidReach CRM remembered session"); } }
        catch (IOException e) { throw new IllegalStateException("Impossibile salvare la sessione", e); }
    }
    public void forget() {
        try { Files.deleteIfExists(file); }
        catch (IOException e) { throw new IllegalStateException("Impossibile chiudere la sessione", e); }
    }
}
