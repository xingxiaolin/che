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
package org.eclipse.che.ide.ext.help.client.about;

import static java.util.Objects.nonNull;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.ProductInfoDataProvider;
import org.eclipse.che.ide.ext.help.client.AboutResources;
import org.eclipse.che.ide.ext.help.client.HelpExtensionLocalizationConstant;
import org.eclipse.che.ide.ui.window.Window;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * UI for {@link AboutView}.
 *
 * @author Ann Shumilova
 */
@Singleton
public class AboutViewImpl extends Window implements AboutView {
  interface AboutViewImplUiBinder extends UiBinder<Widget, AboutViewImpl> {}

  Button btnOk;
  @UiField Label version;
  @UiField Label revision;
  @UiField Label buildTime;

  @UiField(provided = true)
  AboutLocalizationConstant locale;

  @UiField FlowPanel logoPanel;

  private ActionDelegate delegate;

  @Inject
  public AboutViewImpl(
      ProductInfoDataProvider productInfoDataProvider,
      AboutViewImplUiBinder uiBinder,
      AboutLocalizationConstant locale,
      HelpExtensionLocalizationConstant coreLocale,
      AboutResources aboutResources) {
    this.locale = locale;

    aboutResources.aboutCss().ensureInjected();
    String title = locale.aboutControlTitle() + " " + productInfoDataProvider.getName();
    this.setTitle(title);
    this.setWidget(uiBinder.createAndBindUi(this));
    this.ensureDebugId("aboutView-window");

    btnOk =
        addFooterButton(coreLocale.ok(), "help-about-ok", event -> delegate.onOkClicked(), true);

    final SVGResource logo = productInfoDataProvider.getLogo();
    if (nonNull(logo)) {
      logoPanel.add(new SVGImage(logo));
    }
  }

  /** {@inheritDoc} */
  @Override
  public void setDelegate(ActionDelegate delegate) {
    this.delegate = delegate;
  }

  /** {@inheritDoc} */
  @Override
  public void close() {
    this.hide();
  }

  /** {@inheritDoc} */
  @Override
  public void showDialog() {
    this.show(btnOk);
  }

  @Override
  public void onEnterPress(NativeEvent evt) {
    delegate.onOkClicked();
  }

  /** {@inheritDoc} */
  @Override
  public void setVersion(String version) {
    this.version.setText(version);
  }

  /** {@inheritDoc} */
  @Override
  public void setRevision(String revision) {
    this.revision.setText(revision);
  }

  /** {@inheritDoc} */
  @Override
  public void setTime(String time) {
    this.buildTime.setText(time);
  }
}
