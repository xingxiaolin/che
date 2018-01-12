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
package org.eclipse.che.maven.server;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import org.eclipse.che.maven.data.MavenKey;
import org.eclipse.che.maven.data.MavenProjectProblem;

/**
 * Contains result of invocation of maven server.
 *
 * @author Evgen Vidolob
 */
public class MavenServerResult implements Serializable {
  private static final long serialVersionUID = 1L;

  private final MavenProjectInfo projectInfo;
  private final List<MavenProjectProblem> problems;
  private final Set<MavenKey> unresolvedArtifacts;

  public MavenServerResult(
      MavenProjectInfo projectInfo,
      List<MavenProjectProblem> problems,
      Set<MavenKey> unresolvedArtifacts) {
    this.projectInfo = projectInfo;
    this.problems = problems;
    this.unresolvedArtifacts = unresolvedArtifacts;
  }

  public MavenProjectInfo getProjectInfo() {
    return projectInfo;
  }

  public List<MavenProjectProblem> getProblems() {
    return problems;
  }

  public Set<MavenKey> getUnresolvedArtifacts() {
    return unresolvedArtifacts;
  }
}
