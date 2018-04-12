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
package org.eclipse.che.selenium.core.factory;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import javax.inject.Named;
import org.eclipse.che.api.core.rest.HttpJsonRequest;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.core.rest.HttpJsonResponse;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.factory.shared.dto.AuthorDto;
import org.eclipse.che.api.factory.shared.dto.ButtonDto;
import org.eclipse.che.api.factory.shared.dto.FactoryDto;
import org.eclipse.che.api.factory.shared.dto.IdeDto;
import org.eclipse.che.api.factory.shared.dto.PoliciesDto;
import org.eclipse.che.api.workspace.shared.dto.WorkspaceConfigDto;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.dto.server.DtoFactory;
import org.eclipse.che.selenium.core.SeleniumWebDriver;
import org.eclipse.che.selenium.core.client.TestFactoryServiceClient;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.entrance.Entrance;
import org.eclipse.che.selenium.core.provider.TestApiEndpointUrlProvider;
import org.eclipse.che.selenium.core.provider.TestDashboardUrlProvider;
import org.eclipse.che.selenium.core.provider.TestIdeUrlProvider;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.core.webdriver.SeleniumWebDriverHelper;

/** @author Anatolii Bazko */
@Singleton
public class TestFactoryInitializer {

  @Inject private TestUser defaultUser;
  @Inject private TestIdeUrlProvider ideUrlProvider;
  @Inject private TestDashboardUrlProvider dashboardUrlProvider;
  @Inject private TestApiEndpointUrlProvider apiEndpointProvider;
  @Inject private HttpJsonRequestFactory requestFactory;
  @Inject private TestFactoryServiceClient testFactoryServiceClient;
  @Inject private TestWorkspaceServiceClient workspaceServiceClient;
  @Inject private Entrance entrance;
  @Inject private SeleniumWebDriver seleniumWebDriver;
  @Inject private SeleniumWebDriverHelper seleniumWebDriverHelper;

  @Inject
  @Named("che.infrastructure")
  private String infrastructure;

  /**
   * Initialize {@link TestFactory} base upon template.
   *
   * @see FactoryTemplate
   */
  public TestFactoryBuilder fromTemplate(String template) throws Exception {
    String name = NameGenerator.generate("factory", 6);
    InputStream resource =
        TestFactory.class.getResourceAsStream(
            format("/templates/factory/%s/%s", infrastructure, template));
    if (resource == null) {
      throw new IOException(format("Factory template '%s' not found", template));
    }

    String factoryTemplate = IoUtil.readStream(resource);
    FactoryDto factoryDto =
        DtoFactory.getInstance()
            .createDtoFromJson(factoryTemplate, FactoryDto.class)
            .withName(name);
    factoryDto.getWorkspace().setName(name);
    return new TestFactoryBuilder(factoryDto);
  }

  /** Initialize {@link TestFactory} base upon url. Can't be modified. */
  public TestFactory fromUrl(String url) throws Exception {
    HttpJsonRequest httpJsonRequest =
        requestFactory.fromUrl(apiEndpointProvider.get() + "factory/resolver");
    httpJsonRequest.setBody(singletonMap("url", url));
    HttpJsonResponse response = httpJsonRequest.request();

    FactoryDto factoryDto = response.asDto(FactoryDto.class);
    String factoryUrl = ideUrlProvider.get() + "f?url=" + URLEncoder.encode(url, "UTF8");
    return new TestFactory(
        factoryUrl,
        defaultUser,
        factoryDto,
        dashboardUrlProvider,
        testFactoryServiceClient,
        workspaceServiceClient,
        entrance,
        seleniumWebDriver,
        seleniumWebDriverHelper);
  }

  /** Builder for {@link TestFactory}. */
  public class TestFactoryBuilder implements FactoryDto {
    private final FactoryDto factoryDto;

    private TestFactoryBuilder(FactoryDto factoryDto) {
      this.factoryDto = factoryDto;
    }

    public TestFactory build() throws Exception {
      String factoryUrl = testFactoryServiceClient.createFactory(factoryDto);
      return new TestFactory(
          factoryUrl,
          defaultUser,
          factoryDto,
          dashboardUrlProvider,
          testFactoryServiceClient,
          workspaceServiceClient,
          entrance,
          seleniumWebDriver,
          seleniumWebDriverHelper);
    }

    @Override
    public List<Link> getLinks() {
      return factoryDto.getLinks();
    }

    @Override
    public void setLinks(List<Link> links) {
      factoryDto.setLinks(links);
    }

    @Override
    public List<Link> getLinks(String rel) {
      return factoryDto.getLinks(rel);
    }

    @Override
    public Link getLink(String rel) {
      return factoryDto.getLink(rel);
    }

    @Override
    public String getV() {
      return factoryDto.getV();
    }

    @Override
    public void setV(String v) {
      factoryDto.setV(v);
    }

    @Override
    public FactoryDto withV(String v) {
      return factoryDto.withV(v);
    }

    @Override
    public WorkspaceConfigDto getWorkspace() {
      return factoryDto.getWorkspace();
    }

    @Override
    public void setWorkspace(WorkspaceConfigDto workspace) {
      factoryDto.setWorkspace(workspace);
    }

    @Override
    public FactoryDto withWorkspace(WorkspaceConfigDto workspace) {
      return factoryDto.withWorkspace(workspace);
    }

    @Override
    public PoliciesDto getPolicies() {
      return factoryDto.getPolicies();
    }

    @Override
    public void setPolicies(PoliciesDto policies) {
      factoryDto.setPolicies(policies);
    }

    @Override
    public FactoryDto withPolicies(PoliciesDto policies) {
      return factoryDto.withPolicies(policies);
    }

    @Override
    public AuthorDto getCreator() {
      return factoryDto.getCreator();
    }

    @Override
    public void setCreator(AuthorDto creator) {
      factoryDto.setCreator(creator);
    }

    @Override
    public FactoryDto withCreator(AuthorDto creator) {
      return factoryDto.withCreator(creator);
    }

    @Override
    public ButtonDto getButton() {
      return factoryDto.getButton();
    }

    @Override
    public void setButton(ButtonDto button) {
      factoryDto.setButton(button);
    }

    @Override
    public FactoryDto withButton(ButtonDto button) {
      return factoryDto.withButton(button);
    }

    @Override
    public IdeDto getIde() {
      return factoryDto.getIde();
    }

    @Override
    public void setIde(IdeDto ide) {
      factoryDto.setIde(ide);
    }

    @Override
    public FactoryDto withIde(IdeDto ide) {
      return factoryDto.withIde(ide);
    }

    @Override
    public String getId() {
      return factoryDto.getId();
    }

    @Override
    public void setId(String id) {
      factoryDto.setId(id);
    }

    @Override
    public FactoryDto withId(String id) {
      return factoryDto.withId(id);
    }

    @Override
    public String getName() {
      return factoryDto.getName();
    }

    @Override
    public void setName(String name) {
      factoryDto.setName(name);
    }

    @Override
    public FactoryDto withName(String name) {
      return factoryDto.withName(name);
    }

    @Override
    public FactoryDto withLinks(List<Link> links) {
      return factoryDto.withLinks(links);
    }
  }
}
