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
package org.eclipse.che.ide.ext.java.client.tree.library;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.eclipse.che.ide.api.resources.ResourceDelta.UPDATED;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.resources.ResourceChangedEvent;
import org.eclipse.che.ide.api.resources.ResourceChangedEvent.ResourceChangedHandler;
import org.eclipse.che.ide.api.resources.ResourceDelta;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.navigation.service.JavaNavigationService;
import org.eclipse.che.ide.ext.java.client.tree.JavaNodeFactory;
import org.eclipse.che.ide.ext.java.shared.Jar;
import org.eclipse.che.ide.project.node.SyntheticNode;
import org.eclipse.che.ide.project.node.SyntheticNodeUpdateEvent;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.ui.smartTree.data.Node;
import org.eclipse.che.ide.ui.smartTree.data.settings.NodeSettings;
import org.eclipse.che.ide.ui.smartTree.presentation.NodePresentation;

/** @author Vlad Zhukovskiy */
@Beta
public class LibrariesNode extends SyntheticNode<Path> implements ResourceChangedHandler {

  private final JavaNavigationService service;
  private final JavaNodeFactory nodeFactory;
  private final JavaResources javaResources;
  private EventBus eventBus;

  @Inject
  public LibrariesNode(
      @Assisted Path project,
      @Assisted NodeSettings nodeSettings,
      JavaNavigationService service,
      JavaNodeFactory nodeFactory,
      JavaResources javaResources,
      EventBus eventBus) {
    super(project, nodeSettings);
    this.service = service;
    this.nodeFactory = nodeFactory;
    this.javaResources = javaResources;
    this.eventBus = eventBus;

    eventBus.addHandler(ResourceChangedEvent.getType(), this);
  }

  @Override
  protected Promise<List<Node>> getChildrenImpl() {

    return service
        .getExternalLibraries(getData())
        .then(
            new Function<List<Jar>, List<Node>>() {
              @Override
              public List<Node> apply(List<Jar> jars) throws FunctionException {
                List<Node> nodes = newArrayListWithCapacity(jars.size());

                for (Jar jar : jars) {
                  nodes.add(nodeFactory.newJarNode(jar, getData(), getSettings()));
                }

                return nodes;
              }
            });
  }

  @Override
  public void updatePresentation(@NotNull NodePresentation presentation) {
    presentation.setPresentableIcon(javaResources.externalLibraries());
    presentation.setPresentableText(getName());
  }

  @NotNull
  @Override
  public String getName() {
    return "External Libraries";
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public void onResourceChanged(ResourceChangedEvent event) {
    final ResourceDelta delta = event.getDelta();

    if (delta.getKind() == UPDATED && delta.getResource().getLocation().equals(getData())) {
      eventBus.fireEvent(new SyntheticNodeUpdateEvent(LibrariesNode.this));
    }
  }

  @Override
  public Path getProject() {
    return getData();
  }
}
