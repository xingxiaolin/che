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
package org.eclipse.che.plugin.java.server.rest;

import static org.eclipse.che.api.fs.server.WsPathUtils.absolutize;
import static org.eclipse.che.api.fs.server.WsPathUtils.resolve;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.fs.server.FsManager;
import org.eclipse.che.api.fs.server.PathTransformer;
import org.eclipse.che.ide.ext.java.shared.dto.Change;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.corext.format.Formatter;
import org.eclipse.jface.text.BadLocationException;

/**
 * Java formatter service. Have a functionality to format java code and update formatter
 * configuration for the project or for whole workspace.
 */
@Path("java/formatter/")
public class JavaFormatterService {

  private static final String CHE_FOLDER = ".che";
  private static final String CHE_FORMATTER_XML = "che-formatter.xml";
  private static final JavaModel model = JavaModelManager.getJavaModelManager().getJavaModel();

  private final FsManager fsManager;
  private final PathTransformer pathTransformer;
  private Formatter formatter;

  @Inject
  public JavaFormatterService(
      FsManager fsManager, PathTransformer pathTransformer, Formatter formatter) {
    this.fsManager = fsManager;
    this.pathTransformer = pathTransformer;
    this.formatter = formatter;
  }

  @POST
  @Path("/format")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces({MediaType.APPLICATION_JSON})
  @ApiOperation(value = "Creates edits that describe how to format the given string")
  @ApiResponses({
    @ApiResponse(code = 200, message = "The response contains all changes after formating"),
    @ApiResponse(code = 500, message = "Internal server error occurred")
  })
  public List<Change> getFormatChanges(
      @ApiParam(value = "Path to the root project") @QueryParam("projectpath") String projectPath,
      @ApiParam(value = "The given offset to start recording the edits (inclusive)")
          @QueryParam("offset")
          int offset,
      @ApiParam(value = "The given length to stop recording the edits (exclusive)")
          @QueryParam("length")
          int length,
      @ApiParam(value = "The content to format. Java code formatting is supported only")
          String content)
      throws BadLocationException, IllegalArgumentException {
    IJavaProject javaProject = model.getJavaProject(projectPath);
    String formatterPath = CHE_FOLDER + '/' + CHE_FORMATTER_XML;
    File file = null;
    IFile iFile = javaProject.getProject().getFile(formatterPath);
    if (iFile != null) {
      file = iFile.getLocation().toFile();
    }
    if (file == null || !file.exists()) {
      file = getFormatterFromRootFolder(formatterPath);
    }
    return formatter.getFormatChanges(file, content, offset, length);
  }

  @POST
  @Path("update/workspace")
  @Consumes(MediaType.TEXT_PLAIN)
  @ApiOperation(value = "Updates configuration of the jav formatter for the workspace")
  @ApiResponses({
    @ApiResponse(code = 200, message = "Formatter was imported successfully"),
    @ApiResponse(code = 500, message = "Internal server error occurred")
  })
  public void updateRootFormatter(
      @ApiParam(value = "The content of the formatter. Eclipse code formatting is supported only")
          String content)
      throws ServerException {
    try {
      String rootCheFolderWsPath = absolutize(CHE_FOLDER);

      if (!fsManager.existsAsDir(rootCheFolderWsPath)) {
        fsManager.createDir(rootCheFolderWsPath);
      }

      String cheFormatterWsPath = resolve(rootCheFolderWsPath, CHE_FORMATTER_XML);

      if (!fsManager.existsAsFile(cheFormatterWsPath)) {
        fsManager.createFile(cheFormatterWsPath, content);
      } else {
        fsManager.update(cheFormatterWsPath, content);
      }
    } catch (ServerException | ConflictException | NotFoundException e) {
      throw new ServerException(e);
    }
  }

  @POST
  @Path("update/project")
  @Consumes(MediaType.TEXT_PLAIN)
  @ApiOperation(value = "Updates configuration of the jav formatter for the project")
  @ApiResponses({
    @ApiResponse(code = 200, message = "Formatter was imported successfully"),
    @ApiResponse(code = 404, message = "The project was not found"),
    @ApiResponse(code = 500, message = "Internal server error occurred")
  })
  public void updateProjectFormatter(
      @ApiParam(value = "Path to the root project") @QueryParam("projectpath") String projectPath,
      @ApiParam(value = "The content of the formatter. Eclipse code formatting is supported only")
          String content)
      throws ServerException, NotFoundException {
    try {
      String projectWsPath = absolutize(projectPath);
      String projectCheFolderWsPath = resolve(projectWsPath, CHE_FOLDER);

      if (!fsManager.existsAsDir(projectCheFolderWsPath)) {
        fsManager.createDir(projectCheFolderWsPath);
      }

      String cheFormatterWsPath = resolve(projectCheFolderWsPath, CHE_FORMATTER_XML);

      if (!fsManager.existsAsFile(cheFormatterWsPath)) {
        fsManager.createFile(cheFormatterWsPath, content);
      } else {
        fsManager.update(cheFormatterWsPath, content);
      }

    } catch (ConflictException | NotFoundException e) {
      throw new ServerException(e);
    }
  }

  private File getFormatterFromRootFolder(String formatterPath) {

    return fsManager.toIoFile(absolutize(formatterPath));
  }
}
