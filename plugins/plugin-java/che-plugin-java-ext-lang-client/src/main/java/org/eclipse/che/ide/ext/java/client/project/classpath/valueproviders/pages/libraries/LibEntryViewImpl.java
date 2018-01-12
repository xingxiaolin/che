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
package org.eclipse.che.ide.ext.java.client.project.classpath.valueproviders.pages.libraries;

import static com.google.gwt.dom.client.Style.Visibility.HIDDEN;
import static java.util.Collections.reverse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.che.ide.ext.java.client.JavaResources;
import org.eclipse.che.ide.ext.java.client.project.classpath.ProjectClasspathResources;
import org.eclipse.che.ide.ext.java.shared.dto.classpath.ClasspathEntryDto;
import org.eclipse.che.ide.project.shared.NodesResources;
import org.eclipse.che.ide.ui.list.CategoriesList;
import org.eclipse.che.ide.ui.list.Category;
import org.eclipse.che.ide.ui.list.CategoryRenderer;

/**
 * The implementation of {@link LibEntryView}.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class LibEntryViewImpl extends Composite implements LibEntryView {
  private static LibPropertyViewImplUiBinder ourUiBinder =
      GWT.create(LibPropertyViewImplUiBinder.class);
  private final JavaResources javaResources;
  private final NodesResources nodesResources;
  private final ProjectClasspathResources classpathResources;

  @UiField(provided = true)
  ProjectClasspathResources.ClasspathStyles styles;

  @UiField FlowPanel buttonsPanel;
  @UiField FlowPanel libraryPanel;
  @UiField Button addJarBtn;

  private final Category.CategoryEventDelegate<ClasspathEntryDto> librariesDelegate =
      new Category.CategoryEventDelegate<ClasspathEntryDto>() {
        @Override
        public void onListItemClicked(Element listItemBase, ClasspathEntryDto itemData) {
          listItemBase.getStyle().setBackgroundColor("inherit");
        }
      };

  private final CategoryRenderer<ClasspathEntryDto> classpathEntryRenderer =
      new CategoryRenderer<ClasspathEntryDto>() {
        @Override
        public void renderElement(Element element, ClasspathEntryDto data) {
          SpanElement categoryHeaderElement = Document.get().createSpanElement();
          categoryHeaderElement.setClassName(classpathResources.getCss().elementHeader());
          element.appendChild(categoryHeaderElement);

          SpanElement iconElement = Document.get().createSpanElement();
          if (data.getPath().endsWith(".jar")) {
            iconElement.appendChild(javaResources.jarFileIcon().getSvg().getElement());
          }
          categoryHeaderElement.appendChild(iconElement);

          Element textElement = createNameOfElement(data.getPath());
          textElement.addClassName(classpathResources.getCss().elementLabel());
          categoryHeaderElement.appendChild(textElement);

          element.appendChild(categoryHeaderElement);
        }

        @Override
        public Element renderCategory(final Category<ClasspathEntryDto> category) {
          DivElement categoryHeaderElement = Document.get().createDivElement();

          categoryHeaderElement.setClassName(classpathResources.getCss().categoryHeader());

          SpanElement iconElement = Document.get().createSpanElement();
          categoryHeaderElement.appendChild(iconElement);

          SpanElement textElement = Document.get().createSpanElement();
          categoryHeaderElement.appendChild(textElement);
          Element text = createNameOfElement(category.getTitle());
          text.addClassName(classpathResources.getCss().classpathCategoryLabel());
          textElement.appendChild(text);

          final SpanElement buttonElement = Document.get().createSpanElement();
          buttonElement.addClassName(classpathResources.getCss().removeButton());
          buttonElement.appendChild(classpathResources.removeNode().getSvg().getElement());
          buttonElement.getStyle().setVisibility(HIDDEN);
          categoryHeaderElement.appendChild(buttonElement);

          Event.sinkEvents(categoryHeaderElement, Event.MOUSEEVENTS);
          Event.setEventListener(
              categoryHeaderElement,
              new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                  if (!delegate.isPlainJava() || !category.getData().isEmpty()) {
                    return;
                  }
                  if (Event.ONMOUSEOVER == event.getTypeInt()) {
                    buttonElement.getStyle().setVisibility(Style.Visibility.VISIBLE);
                  } else if (Event.ONMOUSEOUT == event.getTypeInt()) {
                    buttonElement.getStyle().setVisibility(HIDDEN);
                  }
                }
              });

          Event.sinkEvents(buttonElement, Event.ONCLICK);
          Event.setEventListener(
              buttonElement,
              new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                  if (Event.ONCLICK == event.getTypeInt()) {
                    event.stopPropagation();
                    delegate.onRemoveClicked(category.getTitle());
                  }
                }
              });

          if (category.getData().isEmpty()) {
            iconElement.appendChild(
                category.getTitle().endsWith(".jar")
                    ? javaResources.jarFileIcon().getSvg().getElement()
                    : nodesResources.simpleFolder().getSvg().getElement());
          } else {
            iconElement.appendChild(javaResources.externalLibraries().getSvg().getElement());
          }

          return categoryHeaderElement;
        }
      };

  private Element createNameOfElement(String fullPath) {
    DivElement textElement = Document.get().createDivElement();

    int lastSeparator = fullPath.lastIndexOf('/');
    if (lastSeparator < 0 || !fullPath.endsWith(".jar")) {
      textElement.setInnerText(fullPath);
      return textElement;
    }

    String name = fullPath.substring(lastSeparator + 1);
    String path = fullPath.substring(0, lastSeparator);
    textElement.setInnerText(name + " - " + path);

    return textElement;
  }

  private CategoriesList list;
  private List<Category<?>> categoriesList;
  private ActionDelegate delegate;

  @Inject
  public LibEntryViewImpl(
      org.eclipse.che.ide.Resources resources,
      JavaResources javaResources,
      NodesResources nodesResources,
      ProjectClasspathResources classpathResources) {
    this.javaResources = javaResources;
    this.nodesResources = nodesResources;
    this.classpathResources = classpathResources;
    styles = classpathResources.getCss();
    styles.ensureInjected();

    initWidget(ourUiBinder.createAndBindUi(this));

    list = new CategoriesList(resources);
    categoriesList = new ArrayList<>();
    libraryPanel.add(list);

    addJarBtn.addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            delegate.onAddJarClicked();
          }
        });
  }

  @Override
  public void setDelegate(ActionDelegate delegate) {
    this.delegate = delegate;
  }

  @Override
  public void renderLibraries() {
    list.clear();
    list.render(categoriesList, false);
  }

  @Override
  public void setData(Map<String, ClasspathEntryDto> data) {
    categoriesList.clear();
    for (Map.Entry<String, ClasspathEntryDto> elem : data.entrySet()) {
      categoriesList.add(
          new Category<>(
              elem.getKey(),
              classpathEntryRenderer,
              elem.getValue().getExpandedEntries(),
              librariesDelegate));

      reverse(categoriesList);
    }
  }

  @Override
  public void setAddJarButtonState(boolean enabled) {
    addJarBtn.setEnabled(enabled);
  }

  interface LibPropertyViewImplUiBinder extends UiBinder<Widget, LibEntryViewImpl> {}
}
