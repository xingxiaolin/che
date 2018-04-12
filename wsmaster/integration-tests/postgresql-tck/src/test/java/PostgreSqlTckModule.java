/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */

import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import org.eclipse.che.account.spi.AccountDao;
import org.eclipse.che.account.spi.AccountImpl;
import org.eclipse.che.account.spi.jpa.JpaAccountDao;
import org.eclipse.che.api.core.model.workspace.Workspace;
import org.eclipse.che.api.installer.server.jpa.JpaInstallerDao;
import org.eclipse.che.api.installer.server.model.impl.InstallerImpl;
import org.eclipse.che.api.installer.server.model.impl.InstallerServerConfigImpl;
import org.eclipse.che.api.installer.server.spi.InstallerDao;
import org.eclipse.che.api.ssh.server.jpa.JpaSshDao;
import org.eclipse.che.api.ssh.server.model.impl.SshPairImpl;
import org.eclipse.che.api.ssh.server.spi.SshDao;
import org.eclipse.che.api.user.server.jpa.JpaPreferenceDao;
import org.eclipse.che.api.user.server.jpa.JpaProfileDao;
import org.eclipse.che.api.user.server.jpa.JpaUserDao;
import org.eclipse.che.api.user.server.jpa.PreferenceEntity;
import org.eclipse.che.api.user.server.model.impl.ProfileImpl;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.api.user.server.spi.PreferenceDao;
import org.eclipse.che.api.user.server.spi.ProfileDao;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.eclipse.che.api.workspace.activity.JpaWorkspaceActivityDao;
import org.eclipse.che.api.workspace.activity.WorkspaceActivityDao;
import org.eclipse.che.api.workspace.activity.WorkspaceExpiration;
import org.eclipse.che.api.workspace.server.jpa.JpaStackDao;
import org.eclipse.che.api.workspace.server.jpa.JpaWorkspaceDao;
import org.eclipse.che.api.workspace.server.model.impl.CommandImpl;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentImpl;
import org.eclipse.che.api.workspace.server.model.impl.MachineConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.ProjectConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.RecipeImpl;
import org.eclipse.che.api.workspace.server.model.impl.ServerConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.SourceStorageImpl;
import org.eclipse.che.api.workspace.server.model.impl.VolumeImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceImpl;
import org.eclipse.che.api.workspace.server.model.impl.stack.StackImpl;
import org.eclipse.che.api.workspace.server.spi.StackDao;
import org.eclipse.che.api.workspace.server.spi.WorkspaceDao;
import org.eclipse.che.commons.lang.Pair;
import org.eclipse.che.commons.test.db.PersistTestModuleBuilder;
import org.eclipse.che.commons.test.tck.JpaCleaner;
import org.eclipse.che.commons.test.tck.TckModule;
import org.eclipse.che.commons.test.tck.TckResourcesCleaner;
import org.eclipse.che.commons.test.tck.repository.JpaTckRepository;
import org.eclipse.che.commons.test.tck.repository.TckRepository;
import org.eclipse.che.commons.test.tck.repository.TckRepositoryException;
import org.eclipse.che.core.db.DBInitializer;
import org.eclipse.che.core.db.postgresql.jpa.eclipselink.PostgreSqlExceptionHandler;
import org.eclipse.che.core.db.schema.SchemaInitializer;
import org.eclipse.che.core.db.schema.impl.flyway.FlywaySchemaInitializer;
import org.eclipse.che.multiuser.machine.authentication.server.signature.jpa.JpaSignatureKeyDao;
import org.eclipse.che.multiuser.machine.authentication.server.signature.model.impl.SignatureKeyImpl;
import org.eclipse.che.multiuser.machine.authentication.server.signature.model.impl.SignatureKeyPairImpl;
import org.eclipse.che.multiuser.machine.authentication.server.signature.spi.SignatureKeyDao;
import org.eclipse.che.security.PasswordEncryptor;
import org.eclipse.che.security.SHA512PasswordEncryptor;
import org.postgresql.Driver;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for running TCKs based on PostgreSQL.
 *
 * @author Yevhenii Voevodin
 */
public class PostgreSqlTckModule extends TckModule {

  private static final Logger LOG = LoggerFactory.getLogger(PostgreSqlTckModule.class);

  @Override
  protected void configure() {
    final String dbUrl = System.getProperty("jdbc.url");
    final String dbUser = System.getProperty("jdbc.user");
    final String dbPassword = System.getProperty("jdbc.password");

    waitConnectionIsEstablished(dbUrl, dbUser, dbPassword);

    // jpa
    install(
        new PersistTestModuleBuilder()
            .setDriver(Driver.class)
            .setUrl(dbUrl)
            .setUser(dbUser)
            .setPassword(dbPassword)
            .setExceptionHandler(PostgreSqlExceptionHandler.class)
            .addEntityClasses(
                AccountImpl.class,
                UserImpl.class,
                ProfileImpl.class,
                PreferenceEntity.class,
                WorkspaceImpl.class,
                WorkspaceConfigImpl.class,
                ProjectConfigImpl.class,
                EnvironmentImpl.class,
                RecipeImpl.class,
                MachineConfigImpl.class,
                SourceStorageImpl.class,
                ServerConfigImpl.class,
                StackImpl.class,
                CommandImpl.class,
                SshPairImpl.class,
                InstallerImpl.class,
                InstallerServerConfigImpl.class,
                WorkspaceExpiration.class,
                VolumeImpl.class,
                SignatureKeyImpl.class,
                SignatureKeyPairImpl.class)
            .addEntityClass(
                "org.eclipse.che.api.workspace.server.model.impl.ProjectConfigImpl$Attribute")
            .build());
    bind(TckResourcesCleaner.class).to(JpaCleaner.class);

    // db initialization
    bind(DBInitializer.class).asEagerSingleton();
    final PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUser(dbUser);
    dataSource.setPassword(dbPassword);
    dataSource.setUrl(dbUrl);
    bind(SchemaInitializer.class).toInstance(new FlywaySchemaInitializer(dataSource, "che-schema"));

    // account
    bind(AccountDao.class).to(JpaAccountDao.class);
    bind(new TypeLiteral<TckRepository<AccountImpl>>() {})
        .toInstance(new JpaTckRepository<>(AccountImpl.class));

    // user
    bind(UserDao.class).to(JpaUserDao.class);
    bind(ProfileDao.class).to(JpaProfileDao.class);
    bind(PreferenceDao.class).to(JpaPreferenceDao.class);
    bind(new TypeLiteral<TckRepository<UserImpl>>() {}).to(UserRepo.class);
    bind(new TypeLiteral<TckRepository<Pair<String, Map<String, String>>>>() {})
        .to(PreferencesRepo.class);
    bind(new TypeLiteral<TckRepository<ProfileImpl>>() {})
        .toInstance(new JpaTckRepository<>(ProfileImpl.class));
    bind(PasswordEncryptor.class).to(SHA512PasswordEncryptor.class);

    // machine
    bind(new TypeLiteral<TckRepository<RecipeImpl>>() {})
        .toInstance(new JpaTckRepository<>(RecipeImpl.class));
    bind(new TypeLiteral<TckRepository<Workspace>>() {})
        .toInstance(new WorkspaceRepoForSnapshots());

    // ssh
    bind(SshDao.class).to(JpaSshDao.class);
    bind(new TypeLiteral<TckRepository<SshPairImpl>>() {})
        .toInstance(new JpaTckRepository<>(SshPairImpl.class));

    // workspace
    bind(WorkspaceDao.class).to(JpaWorkspaceDao.class);
    bind(StackDao.class).to(JpaStackDao.class);
    bind(WorkspaceActivityDao.class).to(JpaWorkspaceActivityDao.class);
    bind(new TypeLiteral<TckRepository<WorkspaceImpl>>() {}).toInstance(new WorkspaceRepository());
    bind(new TypeLiteral<TckRepository<StackImpl>>() {}).toInstance(new StackRepository());
    bind(new TypeLiteral<TckRepository<WorkspaceExpiration>>() {})
        .toInstance(new JpaTckRepository<>(WorkspaceExpiration.class));

    // installer
    bind(InstallerDao.class).to(JpaInstallerDao.class);
    bind(new TypeLiteral<TckRepository<InstallerImpl>>() {})
        .toInstance(new JpaTckRepository<>(InstallerImpl.class));

    // sign keys
    bind(SignatureKeyDao.class).to(JpaSignatureKeyDao.class);
    bind(new TypeLiteral<TckRepository<SignatureKeyPairImpl>>() {})
        .toInstance(new JpaTckRepository<>(SignatureKeyPairImpl.class));
  }

  private static void waitConnectionIsEstablished(String dbUrl, String dbUser, String dbPassword) {
    boolean isAvailable = false;
    for (int i = 0; i < 60 && !isAvailable; i++) {
      try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
        isAvailable = true;
      } catch (SQLException x) {
        LOG.warn(
            "An attempt to connect to the database failed with an error: {}",
            x.getLocalizedMessage());
        try {
          TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException interruptedX) {
          throw new RuntimeException(interruptedX.getLocalizedMessage(), interruptedX);
        }
      }
    }
    if (!isAvailable) {
      throw new IllegalStateException("Couldn't initialize connection with a database");
    }
  }

  @Transactional
  static class PreferencesRepo implements TckRepository<Pair<String, Map<String, String>>> {

    @Inject private Provider<EntityManager> managerProvider;

    @Override
    public void createAll(Collection<? extends Pair<String, Map<String, String>>> entities)
        throws TckRepositoryException {
      final EntityManager manager = managerProvider.get();
      for (Pair<String, Map<String, String>> pair : entities) {
        manager.persist(new PreferenceEntity(pair.first, pair.second));
      }
    }

    @Override
    public void removeAll() throws TckRepositoryException {
      final EntityManager manager = managerProvider.get();
      manager
          .createQuery("SELECT preferences FROM Preference preferences", PreferenceEntity.class)
          .getResultList()
          .forEach(manager::remove);
    }
  }

  @Transactional
  static class UserRepo implements TckRepository<UserImpl> {

    @Inject private Provider<EntityManager> managerProvider;

    @Inject private PasswordEncryptor encryptor;

    @Override
    public void createAll(Collection<? extends UserImpl> entities) throws TckRepositoryException {
      final EntityManager manager = managerProvider.get();
      entities
          .stream()
          .map(
              user ->
                  new UserImpl(
                      user.getId(),
                      user.getEmail(),
                      user.getName(),
                      encryptor.encrypt(user.getPassword()),
                      user.getAliases()))
          .forEach(manager::persist);
    }

    @Override
    public void removeAll() throws TckRepositoryException {
      managerProvider
          .get()
          .createQuery("SELECT u FROM Usr u", UserImpl.class)
          .getResultList()
          .forEach(managerProvider.get()::remove);
    }
  }

  static class WorkspaceRepoForSnapshots extends JpaTckRepository<Workspace> {
    public WorkspaceRepoForSnapshots() {
      super(WorkspaceImpl.class);
    }

    @Override
    public void createAll(Collection<? extends Workspace> entities) throws TckRepositoryException {
      super.createAll(
          entities
              .stream()
              .map(
                  w ->
                      new WorkspaceImpl(
                          w, new AccountImpl(w.getNamespace(), w.getNamespace(), "simple")))
              .collect(Collectors.toList()));
    }
  }

  private static class WorkspaceRepository extends JpaTckRepository<WorkspaceImpl> {
    public WorkspaceRepository() {
      super(WorkspaceImpl.class);
    }

    @Override
    public void createAll(Collection<? extends WorkspaceImpl> entities)
        throws TckRepositoryException {
      for (WorkspaceImpl entity : entities) {
        entity.getConfig().getProjects().forEach(ProjectConfigImpl::prePersistAttributes);
      }
      super.createAll(entities);
    }
  }

  private static class StackRepository extends JpaTckRepository<StackImpl> {
    public StackRepository() {
      super(StackImpl.class);
    }

    @Override
    public void createAll(Collection<? extends StackImpl> entities) throws TckRepositoryException {
      for (StackImpl stack : entities) {
        stack.getWorkspaceConfig().getProjects().forEach(ProjectConfigImpl::prePersistAttributes);
      }
      super.createAll(entities);
    }
  }
}
