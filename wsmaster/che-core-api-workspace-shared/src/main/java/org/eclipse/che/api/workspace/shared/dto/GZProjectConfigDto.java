/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.workspace.shared.dto;

import io.swagger.annotations.ApiModelProperty;
import org.eclipse.che.api.core.factory.FactoryParameter;
import org.eclipse.che.api.core.model.project.GZProjectConfig;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.dto.shared.DTO;
import java.util.List;
import java.util.Map;
import static org.eclipse.che.api.core.factory.FactoryParameter.Obligation.MANDATORY;
import static org.eclipse.che.api.core.factory.FactoryParameter.Obligation.OPTIONAL;

/**
 * @author Alexander Garagatyi
 */
@DTO
public interface GZProjectConfigDto extends GZProjectConfig {
    @Override
    @FactoryParameter(obligation = MANDATORY)
    String getName();

    void setName(String name);

    GZProjectConfigDto withName(String name);

    @Override
    @FactoryParameter(obligation = MANDATORY)
    String getPath();

    void setPath(String path);

    GZProjectConfigDto withPath(String path);

    @Override
    @FactoryParameter(obligation = OPTIONAL)
    String getDescription();

    void setDescription(String description);

    GZProjectConfigDto withDescription(String description);

    @Override
    @FactoryParameter(obligation = MANDATORY)
    String getType();

    void setType(String type);

    GZProjectConfigDto withType(String type);

    @Override
    @FactoryParameter(obligation = OPTIONAL)
    List<String> getMixins();

    void setMixins(List<String> mixins);

    GZProjectConfigDto withMixins(List<String> mixins);

    @Override
    @FactoryParameter(obligation = OPTIONAL)
    Map<String, List<String>> getAttributes();

    void setAttributes(Map<String, List<String>> attributes);

    GZProjectConfigDto withAttributes(Map<String, List<String>> attributes);

    @Override
    @FactoryParameter(obligation = OPTIONAL)
    SourceStorageDto getSource();

    void setSource(SourceStorageDto source);

    GZProjectConfigDto withSource(SourceStorageDto source);

    @FactoryParameter(obligation = OPTIONAL)
    List<Link> getLinks();

    void setLinks(List<Link> links);

    GZProjectConfigDto withLinks(List<Link> links);

    /**提供有关项目错误的信息。如果项目没有任何错误，则此字段为空。*/
    /** Provides information about project errors. If project doesn't have any error this field is empty. */
    @ApiModelProperty(value = "Optional information about project errors. If project doesn't have any error this field is empty")
    List<ProjectProblemDto> getProblems();

    /** @see #getProblems */
    void setProblems(List<ProjectProblemDto> problems);

    GZProjectConfigDto withProblems(List<ProjectProblemDto> problems);
}
