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
package org.eclipse.che.plugin.yaml.ide.preferences;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.plugin.yaml.shared.YamlPreference;

/**
 * The view of {@link YamlExtensionManagerPresenter}.
 *
 * @author Joshua Pinkney
 */
public interface YamlExtensionManagerView extends View<YamlExtensionManagerView.ActionDelegate> {
  /** Needs for delegate some function into YamlExtensionManagerPresenter. */
  interface ActionDelegate {
    /**
     * Performs any actions appropriate in response to the user having pressed the Delete button.
     *
     * @param prefKey prefKey what needs to be deleted
     */
    void onDeleteClicked(@NotNull YamlPreference prefKey);

    /**
     * Performs any actions appropriate in response to the user having pressed the Add url button.
     */
    void onAddUrlClicked();

    /** Sets the current state to dirty. */
    void nowDirty();
  }

  /**
   * Set pairs into view.
   *
   * @param pairs available pairs
   */
  void setPairs(@NotNull List<YamlPreference> pairs);
}
