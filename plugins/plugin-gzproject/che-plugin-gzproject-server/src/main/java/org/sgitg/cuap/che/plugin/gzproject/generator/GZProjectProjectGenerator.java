package org.sgitg.cuap.che.plugin.gzproject.generator;

import java.util.Map;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.handlers.CreateProjectHandler;
import org.eclipse.che.api.project.server.type.AttributeValue;
import org.eclipse.che.api.vfs.Path;
import org.eclipse.che.api.vfs.VirtualFileSystem;
import org.eclipse.che.api.vfs.VirtualFileSystemProvider;
import org.sgitg.cuap.che.plugin.gzproject.shared.Constants;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GZProjectProjectGenerator  implements CreateProjectHandler {

    @Inject
    private VirtualFileSystemProvider virtualFileSystemProvider;
    private static final String FILE_NAME = "project.xml";
    private static final Logger LOG = LoggerFactory.getLogger(GZProjectProjectGenerator.class);
    @Override
    public void onCreateProject(Path projectPath,
                                Map<String, AttributeValue> attributes,
                                Map<String, String> options) throws ForbiddenException, ConflictException, ServerException {
    	LOG.info("**************************************************************************************************");
    	LOG.info("projectPath==//"+projectPath.toString());
        VirtualFileSystem vfs = virtualFileSystemProvider.getVirtualFileSystem();
        FolderEntry baseFolder  = new FolderEntry(vfs.getRoot().createFolder(projectPath.toString()));
        baseFolder.createFile(FILE_NAME, getClass().getClassLoader().getResourceAsStream("files/default_gzproject_content"));
    }

    @Override
    public String getProjectType() {
        return Constants.GZPROJECT_PROJECT_TYPE_ID;
    }
}


