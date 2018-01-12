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
package org.eclipse.che.ide.api.editor.texteditor;

import java.util.List;
import org.eclipse.che.ide.api.editor.texteditor.EditorWidget.WidgetInitializedCallback;

/**
 * Interface for {@link EditorWidget} factories.
 *
 * @author "Mickaël Leduque"
 * @author Artem Zatsarynnyi
 */
public interface EditorWidgetFactory<T extends EditorWidget> {

  /**
   * Create an editor instance.
   *
   * @param editorModes the editor modes
   * @param widgetInitializedCallback the callback that will be called when the editor widget is
   *     fully initialize
   * @return an editor instance
   */
  T createEditorWidget(
      List<String> editorModes, WidgetInitializedCallback widgetInitializedCallback);
}
