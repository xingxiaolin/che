package org.eclipse.che.api.core.model.project;

import java.util.List;
import java.util.Map;

public interface GZProjectConfig {
	String getName();

    String getPath();

    String getDescription();

    String getType();

    List<String> getMixins();

    Map<String, List<String>> getAttributes();

    SourceStorage getSource();
}
