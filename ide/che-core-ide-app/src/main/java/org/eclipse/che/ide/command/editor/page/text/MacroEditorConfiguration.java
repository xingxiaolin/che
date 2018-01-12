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
package org.eclipse.che.ide.command.editor.page.text;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.eclipse.che.ide.api.editor.codeassist.CodeAssistProcessor;
import org.eclipse.che.ide.api.editor.editorconfig.DefaultTextEditorConfiguration;
import org.eclipse.che.ide.api.editor.editorconfig.TextEditorConfiguration;
import org.eclipse.che.ide.api.editor.partition.DocumentPartitioner;

/** {@link TextEditorConfiguration} which provides {@link CodeAssistProcessor} for macros names. */
public class MacroEditorConfiguration extends DefaultTextEditorConfiguration {

  private MacroCodeAssistProcessor codeAssistProcessor;

  @Inject
  public MacroEditorConfiguration(MacroCodeAssistProcessor codeAssistProcessor) {
    this.codeAssistProcessor = codeAssistProcessor;
  }

  @Override
  public Map<String, CodeAssistProcessor> getContentAssistantProcessors() {
    Map<String, CodeAssistProcessor> map = new HashMap<>();
    map.put(DocumentPartitioner.DEFAULT_CONTENT_TYPE, codeAssistProcessor);

    return map;
  }
}
