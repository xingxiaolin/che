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
package org.eclipse.che.ide.api.editor.events;

import com.google.gwt.event.shared.GwtEvent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;

/**
 * Fires by editor when change dirty state(content modified or saved)
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class EditorDirtyStateChangedEvent extends GwtEvent<EditorDirtyStateChangedHandler> {

  public static final GwtEvent.Type<EditorDirtyStateChangedHandler> TYPE =
      new Type<EditorDirtyStateChangedHandler>();

  private EditorPartPresenter editor;

  /** @param editor */
  public EditorDirtyStateChangedEvent(EditorPartPresenter editor) {
    super();
    this.editor = editor;
  }

  /** {@inheritDoc} */
  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<EditorDirtyStateChangedHandler>
      getAssociatedType() {
    return TYPE;
  }

  /** {@inheritDoc} */
  @Override
  protected void dispatch(EditorDirtyStateChangedHandler handler) {
    handler.onEditorDirtyStateChanged(this);
  }

  /** @return the editor */
  public EditorPartPresenter getEditor() {
    return editor;
  }
}
