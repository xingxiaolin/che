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
package org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.pages.libraries;

import static org.eclipse.che.ide.ext.java.shared.ClasspathEntryKind.CONTAINER;
import static org.eclipse.che.ide.ext.java.shared.ClasspathEntryKind.LIBRARY;
import static org.eclipse.che.ide.ext.java.shared.Constants.JAVAC;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.command.ClasspathContainer;
import org.eclipse.che.ide.ext.java.client.project.classpath.ClasspathResolver;
import org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.pages.AbstractClasspathPagePresenter;
import org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.selectnode.SelectNodePresenter;
import org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.selectnode.interceptors.JarNodeInterceptor;
import org.eclipse.che.ide.ext.java.shared.ClasspathEntryKind;
import org.eclipse.che.ide.ext.java.shared.dto.classpath.ClasspathEntryDto;

/**
 * The page for the information about libraries which are including into classpath.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class LibEntryPresenter extends AbstractClasspathPagePresenter
    implements LibEntryView.ActionDelegate {
  private final ClasspathContainer classpathContainer;
  private final ClasspathResolver classpathResolver;
  private final DtoFactory dtoFactory;
  private final AppContext appContext;
  private final SelectNodePresenter selectNodePresenter;
  private final LibEntryView view;

  private boolean dirty;
  private boolean isPlainJava;
  private Map<String, ClasspathEntryDto> categories;

  @Inject
  public LibEntryPresenter(
      LibEntryView view,
      ClasspathContainer classpathContainer,
      ClasspathResolver classpathResolver,
      JavaLocalizationConstant localization,
      DtoFactory dtoFactory,
      AppContext appContext,
      SelectNodePresenter selectNodePresenter) {
    super(localization.librariesPropertyName(), localization.javaBuildPathCategory(), null);
    this.view = view;
    this.classpathContainer = classpathContainer;
    this.classpathResolver = classpathResolver;
    this.dtoFactory = dtoFactory;
    this.appContext = appContext;
    this.selectNodePresenter = selectNodePresenter;

    dirty = false;

    categories = new TreeMap<>();
    view.setDelegate(this);
  }

  @Override
  public boolean isDirty() {
    return dirty;
  }

  @Override
  public void go(final AcceptsOneWidget container) {
    final Resource resource = appContext.getResource();

    Preconditions.checkState(resource != null);

    final Optional<Project> project = resource.getRelatedProject();

    isPlainJava = JAVAC.equals(project.get().getType());

    setReadOnlyMod();

    container.setWidget(view);

    if (!categories.isEmpty()) {
      view.renderLibraries();
      return;
    }

    classpathContainer
        .getClasspathEntries(project.get().getPath())
        .then(
            new Operation<List<ClasspathEntryDto>>() {
              @Override
              public void apply(List<ClasspathEntryDto> arg) throws OperationException {
                categories.clear();
                for (ClasspathEntryDto entry : arg) {
                  if (CONTAINER == entry.getEntryKind() || LIBRARY == entry.getEntryKind()) {
                    categories.put(entry.getPath(), entry);
                  }
                }
                view.setData(categories);
                view.renderLibraries();
              }
            });
  }

  @Override
  public void onAddJarClicked() {
    selectNodePresenter.show(this, new JarNodeInterceptor(), false);
  }

  @Override
  public void onRemoveClicked(String path) {
    removeNode(path);
  }

  @Override
  public void storeChanges() {
    classpathResolver.getLibs().clear();
    classpathResolver.getContainers().clear();

    for (Map.Entry<String, ClasspathEntryDto> entry : categories.entrySet()) {
      if (ClasspathEntryKind.LIBRARY == entry.getValue().getEntryKind()) {
        classpathResolver.getLibs().add(entry.getKey());
      } else if (CONTAINER == entry.getValue().getEntryKind()) {
        classpathResolver.getContainers().add(entry.getValue());
      }
    }

    dirty = false;
    delegate.onDirtyChanged();
  }

  @Override
  public void revertChanges() {
    clearData();

    dirty = false;
    delegate.onDirtyChanged();
  }

  @Override
  public void clearData() {
    categories.clear();
  }

  @Override
  public boolean isPlainJava() {
    return isPlainJava;
  }

  @Override
  public void addNode(String path, int kind) {
    if (categories.containsKey(path)) {
      return;
    }

    dirty = true;
    delegate.onDirtyChanged();

    categories.put(path, dtoFactory.createDto(ClasspathEntryDto.class).withEntryKind(kind));
    view.setData(categories);
    view.renderLibraries();
  }

  @Override
  public void removeNode(String path) {
    dirty = true;
    delegate.onDirtyChanged();
    categories.remove(path);
    view.setData(categories);
    view.renderLibraries();
  }

  private void setReadOnlyMod() {
    view.setAddJarButtonState(isPlainJava);
  }
}
