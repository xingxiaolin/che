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
package org.eclipse.che.api.languageserver.maven.plugin;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.che.api.languageserver.generator.ClientDtoGenerator;
import org.eclipse.che.api.languageserver.generator.DtoGenerator;
import org.eclipse.che.api.languageserver.generator.ServerDtoGenerator;

/** Mojo to run {@link org.eclipse.che.api.languageserver.generator.DtoGenerator}. */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.COMPILE)
public class DtoGeneratorMojo extends AbstractMojo {
  @Parameter(property = "outputDir", required = true)
  private String outputDirectory;

  @Parameter(property = "dtoPackages", required = true)
  private String[] dtoPackages;

  @Parameter(property = "classes", required = false)
  private String[] classes = new String[] {};

  @Parameter(property = "genClassName", required = true)
  private String genClassName;

  @Parameter(property = "impl", required = true)
  private String impl;

  @Override
  public void execute() throws MojoExecutionException {
    DtoGenerator generator;
    if ("client".equals(impl)) {
      generator = new ClientDtoGenerator();
    } else {
      generator = new ServerDtoGenerator();
    }

    int lastDot = genClassName.lastIndexOf('.');
    String targetClass = genClassName.substring(lastDot + 1);
    String targetPackage = genClassName.substring(0, lastDot);

    try {
      generator.generate(
          new File(outputDirectory), targetClass, targetPackage, dtoPackages, classes);
    } catch (IOException e) {
      throw new MojoExecutionException("Generation failed", e);
    }
  }
}
