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
package org.eclipse.che.plugin.python.ide;

import static org.eclipse.che.ide.api.action.IdeActions.GROUP_FILE_NEW;
import static org.eclipse.che.plugin.python.shared.ProjectAttributes.PYTHON_CATEGORY;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.plugin.python.ide.action.CreatePythonFileAction;

/**
 * Python extension entry point.
 *
 * @author Valeriy Svydenko
 */
@Extension(title = "Python")
public class PythonExtension {
  @Inject
  public PythonExtension(
      FileTypeRegistry fileTypeRegistry,
      CreatePythonFileAction createPythonFileAction,
      ActionManager actionManager,
      PythonResources pythonResources,
      IconRegistry iconRegistry,
      @Named("PythonFileType") FileType pythonFile) {
    fileTypeRegistry.registerFileType(pythonFile);

    DefaultActionGroup newGroup = (DefaultActionGroup) actionManager.getAction(GROUP_FILE_NEW);
    actionManager.registerAction("pythonFile", createPythonFileAction);
    newGroup.add(createPythonFileAction, Constraints.FIRST);

    iconRegistry.registerIcon(
        new Icon(PYTHON_CATEGORY + ".samples.category.icon", pythonResources.category()));
  }
}
