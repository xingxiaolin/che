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
package org.eclipse.che.ide.projectimport.wizard;

import static org.mockito.Mockito.verify;

import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.wizard.Wizard.CompleteCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Artem Zatsarynnyi
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportWizardTest {

  @Mock private ProjectImporter importer;
  @Mock private MutableProjectConfig projectConfig;
  @Mock private CompleteCallback completeCallback;

  @InjectMocks private ImportWizard wizard;

  @Test
  public void shouldCallImporterOnCompletion() {
    wizard.complete(completeCallback);

    verify(importer).importProject(completeCallback, projectConfig);
  }
}
