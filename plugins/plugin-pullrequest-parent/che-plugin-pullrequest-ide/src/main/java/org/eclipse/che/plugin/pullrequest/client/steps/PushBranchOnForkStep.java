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
package org.eclipse.che.plugin.pullrequest.client.steps;

import com.google.inject.Singleton;
import javax.inject.Inject;
import org.eclipse.che.plugin.pullrequest.client.workflow.Context;
import org.eclipse.che.plugin.pullrequest.client.workflow.Step;
import org.eclipse.che.plugin.pullrequest.client.workflow.WorkflowExecutor;

/**
 * Push the local contribution branch on the user fork.
 *
 * @author Kevin Pollet
 */
@Singleton
public class PushBranchOnForkStep implements Step {

  private final PushBranchStepFactory pushBranchStepFactory;

  @Inject
  public PushBranchOnForkStep(PushBranchStepFactory pushBranchStepFactory) {
    this.pushBranchStepFactory = pushBranchStepFactory;
  }

  @Override
  public void execute(final WorkflowExecutor executor, final Context context) {
    pushBranchStepFactory
        .create(this, context.getHostUserLogin(), context.getForkedRepositoryName())
        .execute(executor, context);
  }
}
