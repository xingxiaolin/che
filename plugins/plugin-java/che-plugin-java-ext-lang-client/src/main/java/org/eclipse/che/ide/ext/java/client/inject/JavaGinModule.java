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
package org.eclipse.che.ide.ext.java.client.inject;

import static org.eclipse.che.ide.ext.java.client.action.OrganizeImportsAction.JAVA_ORGANIZE_IMPORT_ID;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMapBinder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.macro.Macro;
import org.eclipse.che.ide.api.preferences.PreferencePagePresenter;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.api.reference.FqnProvider;
import org.eclipse.che.ide.api.resources.RenamingSupport;
import org.eclipse.che.ide.api.resources.ResourceInterceptor;
import org.eclipse.che.ide.ext.java.client.CurrentClassFQN_Macro;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.action.OrganizeImportsAction;
import org.eclipse.che.ide.ext.java.client.action.ProposalAction;
import org.eclipse.che.ide.ext.java.client.command.JavaCommandType;
import org.eclipse.che.ide.ext.java.client.command.valueproviders.ClasspathMacro;
import org.eclipse.che.ide.ext.java.client.command.valueproviders.MainClassMacro;
import org.eclipse.che.ide.ext.java.client.command.valueproviders.OutputDirMacro;
import org.eclipse.che.ide.ext.java.client.command.valueproviders.SourcepathMacro;
import org.eclipse.che.ide.ext.java.client.documentation.QuickDocPresenter;
import org.eclipse.che.ide.ext.java.client.documentation.QuickDocumentation;
import org.eclipse.che.ide.ext.java.client.inject.factories.PropertyWidgetFactory;
import org.eclipse.che.ide.ext.java.client.navigation.service.JavaNavigationService;
import org.eclipse.che.ide.ext.java.client.navigation.service.JavaNavigationServiceImpl;
import org.eclipse.che.ide.ext.java.client.newsourcefile.NewJavaSourceFileView;
import org.eclipse.che.ide.ext.java.client.newsourcefile.NewJavaSourceFileViewImpl;
import org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.pages.ClasspathPagePresenter;
import org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.pages.libraries.LibEntryPresenter;
import org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.pages.sources.SourceEntryPresenter;
import org.eclipse.che.ide.ext.java.client.reference.JavaFqnProvider;
import org.eclipse.che.ide.ext.java.client.resource.ClassInterceptor;
import org.eclipse.che.ide.ext.java.client.resource.JavaSourceRenameValidator;
import org.eclipse.che.ide.ext.java.client.resource.SourceFolderInterceptor;
import org.eclipse.che.ide.ext.java.client.search.JavaSearchJsonRpcClient;
import org.eclipse.che.ide.ext.java.client.search.JavaSearchService;
import org.eclipse.che.ide.ext.java.client.search.node.NodeFactory;
import org.eclipse.che.ide.ext.java.client.settings.compiler.ErrorsWarningsPreferenceManager;
import org.eclipse.che.ide.ext.java.client.settings.compiler.JavaCompilerPreferenceManager;
import org.eclipse.che.ide.ext.java.client.settings.compiler.JavaCompilerPreferencePresenter;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidget;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidgetImpl;
import org.eclipse.che.ide.ext.java.client.tree.JavaNodeFactory;
import org.eclipse.che.ide.ext.java.client.tree.JavaPackageConnector;
import org.eclipse.che.ide.ext.java.client.tree.LibraryNodeProvider;
import org.eclipse.che.ide.ext.java.client.tree.SourceFolderDecorator;
import org.eclipse.che.ide.ext.java.client.tree.TestFolderDecorator;
import org.eclipse.che.ide.project.node.icon.NodeIconProvider;
import org.eclipse.che.ide.ui.smartTree.data.NodeInterceptor;

/**
 * @author Evgen Vidolob
 * @author Artem Zatsarynnyi
 */
@ExtensionGinModule
public class JavaGinModule extends AbstractGinModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {
    install(new FormatterGinModule());

    GinMapBinder<String, ProposalAction> proposalActionMapBinder =
        GinMapBinder.newMapBinder(binder(), String.class, ProposalAction.class);
    proposalActionMapBinder.addBinding(JAVA_ORGANIZE_IMPORT_ID).to(OrganizeImportsAction.class);

    bind(NewJavaSourceFileView.class).to(NewJavaSourceFileViewImpl.class).in(Singleton.class);
    bind(QuickDocumentation.class).to(QuickDocPresenter.class).in(Singleton.class);
    bind(JavaNavigationService.class).to(JavaNavigationServiceImpl.class);
    bind(JavaSearchService.class).to(JavaSearchJsonRpcClient.class);

    GinMultibinder.newSetBinder(binder(), NodeInterceptor.class)
        .addBinding()
        .to(TestFolderDecorator.class);
    GinMultibinder.newSetBinder(binder(), NodeInterceptor.class)
        .addBinding()
        .to(JavaPackageConnector.class);
    GinMultibinder.newSetBinder(binder(), NodeIconProvider.class)
        .addBinding()
        .to(SourceFolderDecorator.class);

    GinMultibinder.newSetBinder(binder(), NodeInterceptor.class)
        .addBinding()
        .to(LibraryNodeProvider.class);

    GinMultibinder.newSetBinder(binder(), ResourceInterceptor.class)
        .addBinding()
        .to(SourceFolderInterceptor.class);
    GinMultibinder.newSetBinder(binder(), ResourceInterceptor.class)
        .addBinding()
        .to(ClassInterceptor.class);

    GinMultibinder.newSetBinder(binder(), CommandType.class).addBinding().to(JavaCommandType.class);

    GinMapBinder<String, FqnProvider> fqnProviders =
        GinMapBinder.newMapBinder(binder(), String.class, FqnProvider.class);
    fqnProviders.addBinding("maven").to(JavaFqnProvider.class);

    install(new GinFactoryModuleBuilder().build(JavaNodeFactory.class));
    install(
        new GinFactoryModuleBuilder()
            .implement(PropertyWidget.class, PropertyWidgetImpl.class)
            .build(PropertyWidgetFactory.class));

    install(new GinFactoryModuleBuilder().build(NodeFactory.class));
    install(
        new GinFactoryModuleBuilder()
            .build(org.eclipse.che.ide.ext.java.client.navigation.factory.NodeFactory.class));

    GinMultibinder<PreferencePagePresenter> settingsBinder =
        GinMultibinder.newSetBinder(binder(), PreferencePagePresenter.class);
    settingsBinder.addBinding().to(JavaCompilerPreferencePresenter.class);

    bind(PreferencesManager.class)
        .annotatedWith(JavaCompilerPreferenceManager.class)
        .to(ErrorsWarningsPreferenceManager.class);
    GinMultibinder.newSetBinder(binder(), PreferencesManager.class)
        .addBinding()
        .to(ErrorsWarningsPreferenceManager.class);

    GinMultibinder.newSetBinder(binder(), Macro.class).addBinding().to(ClasspathMacro.class);
    GinMultibinder.newSetBinder(binder(), Macro.class).addBinding().to(OutputDirMacro.class);
    GinMultibinder.newSetBinder(binder(), Macro.class).addBinding().to(MainClassMacro.class);
    GinMultibinder.newSetBinder(binder(), Macro.class).addBinding().to(SourcepathMacro.class);
    GinMultibinder.newSetBinder(binder(), Macro.class).addBinding().to(CurrentClassFQN_Macro.class);
    GinMultibinder.newSetBinder(binder(), ClasspathPagePresenter.class)
        .addBinding()
        .to(LibEntryPresenter.class);
    GinMultibinder.newSetBinder(binder(), ClasspathPagePresenter.class)
        .addBinding()
        .to(SourceEntryPresenter.class);
    GinMultibinder.newSetBinder(binder(), RenamingSupport.class)
        .addBinding()
        .to(JavaSourceRenameValidator.class);
  }

  @Provides
  @Singleton
  @Named("JavaFileType")
  protected FileType provideJavaFile() {
    return new FileType(JavaResources.INSTANCE.javaFile(), "java");
  }

  @Provides
  @Singleton
  @Named("JavaClassFileType")
  protected FileType provideJavaClassFile() {
    return new FileType(JavaResources.INSTANCE.javaFile(), "class");
  }

  @Provides
  @Singleton
  @Named("JspFileType")
  protected FileType provideJspFile() {
    return new FileType(JavaResources.INSTANCE.jspFile(), "jsp");
  }

  @Provides
  @Singleton
  @Named("JsfFileType")
  protected FileType provideJsfFile() {
    return new FileType(JavaResources.INSTANCE.jsfFile(), "jsf");
  }
}
