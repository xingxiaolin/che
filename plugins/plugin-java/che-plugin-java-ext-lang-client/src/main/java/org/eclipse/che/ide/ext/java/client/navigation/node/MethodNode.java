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
package org.eclipse.che.ide.ext.java.client.navigation.node;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.navigation.filestructure.FileStructurePresenter;
import org.eclipse.che.ide.ext.java.client.util.Flags;
import org.eclipse.che.ide.ext.java.shared.dto.model.Method;
import org.eclipse.che.ide.ui.smartTree.data.HasAction;
import org.eclipse.che.ide.ui.smartTree.data.Node;
import org.eclipse.che.ide.ui.smartTree.presentation.NodePresentation;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Representation of java method for the java navigation tree.
 *
 * @author Valeriy Svydenko
 */
public class MethodNode extends AbstractPresentationNode implements HasAction {
  private final JavaResources resources;
  private final Method method;
  private final boolean isFromSuper;
  private final FileStructurePresenter fileStructurePresenter;

  private boolean showingInheritedMembers;

  @Inject
  public MethodNode(
      JavaResources resources,
      @Assisted Method method,
      @Assisted("showInheritedMembers") boolean showInheritedMembers,
      @Assisted("isFromSuper") boolean isFromSuper,
      FileStructurePresenter fileStructurePresenter) {
    this.resources = resources;
    this.method = method;
    this.isFromSuper = isFromSuper;
    this.fileStructurePresenter = fileStructurePresenter;
    this.showingInheritedMembers = showInheritedMembers;
  }

  /** {@inheritDoc} */
  @Override
  protected Promise<List<Node>> getChildrenImpl() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void updatePresentation(@NotNull NodePresentation presentation) {
    StringBuilder presentableName =
        new StringBuilder(method.getLabel() + " : " + method.getReturnType());
    if (showingInheritedMembers) {
      String path = method.getRootPath();
      String className =
          method.isBinary()
              ? path.substring(path.lastIndexOf('.') + 1)
              : path.substring(path.lastIndexOf('/') + 1, path.indexOf('.'));

      presentableName.append(" -> ").append(className);
    }

    updatePresentationField(isFromSuper, presentation, presentableName.toString(), resources);

    SVGResource icon;
    int flag = method.getFlags();
    if (Flags.isPublic(flag)) {
      icon = resources.publicMethod();
    } else if (Flags.isPrivate(flag)) {
      icon = resources.privateMethod();
    } else if (Flags.isProtected(flag)) {
      icon = resources.protectedMethod();
    } else {
      icon = resources.publicMethod();
    }
    presentation.setPresentableIcon(icon);
  }

  /** {@inheritDoc} */
  @Override
  public void actionPerformed() {
    fileStructurePresenter.actionPerformed(method);
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return method.getElementName();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isLeaf() {
    return true;
  }
}
