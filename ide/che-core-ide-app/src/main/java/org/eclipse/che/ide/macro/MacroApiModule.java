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
package org.eclipse.che.ide.macro;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.macro.Macro;
import org.eclipse.che.ide.api.macro.MacroProcessor;
import org.eclipse.che.ide.api.macro.MacroRegistry;
import org.eclipse.che.ide.editor.macro.EditorCurrentFileBaseNameMacro;
import org.eclipse.che.ide.editor.macro.EditorCurrentFileNameMacro;
import org.eclipse.che.ide.editor.macro.EditorCurrentFilePathMacro;
import org.eclipse.che.ide.editor.macro.EditorCurrentFileRelativePathMacro;
import org.eclipse.che.ide.editor.macro.EditorCurrentProjectNameMacro;
import org.eclipse.che.ide.editor.macro.EditorCurrentProjectTypeMacro;
import org.eclipse.che.ide.macro.chooser.MacroChooserView;
import org.eclipse.che.ide.macro.chooser.MacroChooserViewImpl;
import org.eclipse.che.ide.part.explorer.project.macro.ExplorerCurrentFileBaseNameMacro;
import org.eclipse.che.ide.part.explorer.project.macro.ExplorerCurrentFileNameMacro;
import org.eclipse.che.ide.part.explorer.project.macro.ExplorerCurrentFileParentPathMacro;
import org.eclipse.che.ide.part.explorer.project.macro.ExplorerCurrentFilePathMacro;
import org.eclipse.che.ide.part.explorer.project.macro.ExplorerCurrentFileRelativePathMacro;
import org.eclipse.che.ide.part.explorer.project.macro.ExplorerCurrentProjectNameMacro;
import org.eclipse.che.ide.part.explorer.project.macro.ExplorerCurrentProjectTypeMacro;

/**
 * GIN module for configuring Macro API components.
 *
 * @author Artem Zatsarynnyi
 */
public class MacroApiModule extends AbstractGinModule {

  @Override
  protected void configure() {
    bind(MacroRegistry.class).to(MacroRegistryImpl.class).in(Singleton.class);

    bind(MacroProcessor.class).to(MacroProcessorImpl.class).in(Singleton.class);

    GinMultibinder<Macro> macrosBinder = GinMultibinder.newSetBinder(binder(), Macro.class);
    macrosBinder.addBinding().to(EditorCurrentFileNameMacro.class);
    macrosBinder.addBinding().to(EditorCurrentFileBaseNameMacro.class);
    macrosBinder.addBinding().to(EditorCurrentFilePathMacro.class);
    macrosBinder.addBinding().to(EditorCurrentFileRelativePathMacro.class);
    macrosBinder.addBinding().to(EditorCurrentProjectNameMacro.class);
    macrosBinder.addBinding().to(EditorCurrentProjectTypeMacro.class);
    macrosBinder.addBinding().to(ExplorerCurrentFileNameMacro.class);
    macrosBinder.addBinding().to(ExplorerCurrentFileBaseNameMacro.class);
    macrosBinder.addBinding().to(ExplorerCurrentFilePathMacro.class);
    macrosBinder.addBinding().to(ExplorerCurrentFileParentPathMacro.class);
    macrosBinder.addBinding().to(ExplorerCurrentFileRelativePathMacro.class);
    macrosBinder.addBinding().to(ExplorerCurrentProjectNameMacro.class);
    macrosBinder.addBinding().to(ExplorerCurrentProjectTypeMacro.class);
    macrosBinder.addBinding().to(WorkspaceNameMacro.class);
    macrosBinder.addBinding().to(WorkspaceNamespaceMacro.class);
    macrosBinder.addBinding().to(DevMachineHostNameMacro.class);
    macrosBinder.addBinding().to(CurrentProjectPathMacro.class);
    macrosBinder.addBinding().to(CurrentProjectEldestParentPathMacro.class);
    macrosBinder.addBinding().to(CurrentProjectRelativePathMacro.class);

    bind(ServerAddressMacroRegistrar.class).asEagerSingleton();

    bind(MacroChooserView.class).to(MacroChooserViewImpl.class);
  }
}
