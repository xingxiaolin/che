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
package org.eclipse.che.plugin.languageserver.ide.editor.signature;

import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.ide.api.editor.signature.ParameterInfo;
import org.eclipse.che.ide.api.editor.signature.SignatureInfo;
import org.eclipse.lsp4j.ParameterInformation;
import org.eclipse.lsp4j.SignatureInformation;

/** @author Evgen Vidolob */
class SignatureInfoImpl implements SignatureInfo {

  private final SignatureInformation dto;

  private List<ParameterInfo> parameterInfos;

  public SignatureInfoImpl(SignatureInformation dto) {
    this.dto = dto;
  }

  @Override
  public String getLabel() {
    return dto.getLabel();
  }

  @Override
  public Optional<String> getDocumentation() {
    return Optional.fromNullable(dto.getDocumentation());
  }

  @Override
  public Optional<List<ParameterInfo>> getParameters() {
    if (parameterInfos == null && dto.getParameters() != null) {
      parameterInfos = new ArrayList<>(dto.getParameters().size());
      for (ParameterInformation informationDTO : dto.getParameters()) {
        parameterInfos.add(new ParamterInfoImpl(informationDTO));
      }
      return Optional.of(parameterInfos);
    }
    return Optional.fromNullable(parameterInfos);
  }
}
