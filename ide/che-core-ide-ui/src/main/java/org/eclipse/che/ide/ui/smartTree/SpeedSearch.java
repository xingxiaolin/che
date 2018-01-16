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
package org.eclipse.che.ide.ui.smartTree;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.che.ide.DelayedTask;
import org.eclipse.che.ide.FontAwesome;
import org.eclipse.che.ide.ui.smartTree.converter.NodeConverter;
import org.eclipse.che.ide.ui.smartTree.converter.impl.NodeNameConverter;
import org.eclipse.che.ide.ui.smartTree.data.Node;
import org.eclipse.che.ide.ui.smartTree.presentation.DefaultPresentationRenderer;
import org.eclipse.che.ide.util.dom.Elements;

import static com.google.gwt.dom.client.Style.BorderStyle.SOLID;
import static com.google.gwt.dom.client.Style.Position.FIXED;
import static com.google.gwt.dom.client.Style.Unit.PX;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_BACKSPACE;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;
import static com.google.gwt.event.dom.client.KeyCodes.KEY_ESCAPE;
import static org.eclipse.che.ide.api.theme.Style.theme;

/** @author Vlad Zhukovskiy */
public class SpeedSearch {

  private Tree tree;
  private final String style;
  private final boolean filterElements;
  private NodeConverter<Node, String> nodeConverter;
  private DelayedTask searchTask;
  private StringBuilder searchRequest;
  private SearchPopUp searchPopUp;
  private static final String ID = "speedSearch";

  private int searchDelay;
  private List<Node> savedNodes;

  private class SearchPopUp extends HorizontalPanel {
    private Label searchText;

    private SearchPopUp() {
      getElement().setId(ID);
      setVisible(false); // by default

      Label icon = new Label();
      icon.getElement().setInnerHTML(FontAwesome.SEARCH);
      Style iconStyle = icon.getElement().getStyle();
      iconStyle.setFontSize(16, PX);
      iconStyle.setMarginLeft(5, PX);
      iconStyle.setMarginRight(5, PX);

      searchText = new Label();
      Style searchTextStyle = searchText.getElement().getStyle();
      searchTextStyle.setFontSize(12, PX);
      searchTextStyle.setMarginRight(5, PX);
      searchTextStyle.setMarginTop(4, PX);

      add(icon);
      add(searchText);
    }

    private void setSearchRequest(String request) {
      searchText.setText(request);
    }
  }

  SpeedSearch(
      Tree tree, String style, NodeConverter<Node, String> nodeConverter, boolean filterElements) {
    this.tree = tree;
    this.style = style;
    this.filterElements = filterElements;
    this.tree.setPresentationRenderer(new SearchRender(tree.getTreeStyles()));
    this.nodeConverter = nodeConverter != null ? nodeConverter : new NodeNameConverter();

    this.tree.addKeyPressHandler(
        event -> {
          event.stopPropagation();
          searchRequest.append(String.valueOf(event.getCharCode()));
          update();
        });

    this.tree.addKeyDownHandler(
        event -> {
          switch (event.getNativeKeyCode()) {
            case KEY_ENTER:
              removeSearchPopUpFromTree();
              break;
            case KEY_BACKSPACE:
              if (!Strings.isNullOrEmpty(searchRequest.toString())) {
                event.preventDefault();
                searchRequest.setLength(searchRequest.length() - 1);
                doSearch();
              }
              break;
            case KEY_ESCAPE:
              if (searchRequest.length() != 0) {
                event.stopPropagation();
                searchRequest.setLength(0);
                doSearch();
              }
              break;
          }
        });

    this.searchDelay = 100; // 100ms
    this.searchRequest = new StringBuilder();
    initSearchPopUp();
  }

  private void initSearchPopUp() {
    this.searchPopUp = new SearchPopUp();
    Style style = this.searchPopUp.getElement().getStyle();

    style.setBackgroundColor(theme.backgroundColor());
    style.setBorderStyle(SOLID);
    style.setBorderColor(theme.getPopupBorderColor());
    style.setBorderWidth(1, PX);
    style.setPadding(2, PX);
    style.setPosition(FIXED);
    style.setBottom(5, PX);
    style.setLeft(20, PX);
  }

  private void addSearchPopUpToTree() {
    if (Document.get().getElementById(ID) == null) {
      searchPopUp.setVisible(true);
      tree.getParent().getElement().appendChild(searchPopUp.getElement());
    }
  }

  private void removeSearchPopUpFromTree() {
    searchRequest.setLength(0);
    Document.get().getElementById(ID).removeFromParent();
  }

  protected void update() {
    if (searchTask == null) {
      searchTask =
          new DelayedTask() {
            @Override
            public void onExecute() {
              doSearch();
            }
          };
    }
    searchTask.delay(searchDelay);
  }

  protected void reset() {
    savedNodes = null;
  }

  private void doSearch() {
    if (Strings.isNullOrEmpty(searchRequest.toString())) {
      removeSearchPopUpFromTree();
    } else {
      addSearchPopUpToTree();
      searchPopUp.setSearchRequest(searchRequest.toString());
    }

    tree.getSelectionModel().deselectAll();

    savedNodes = savedNodes == null ? getVisibleNodes() : savedNodes;

    List<Node> filter =
        savedNodes.stream().filter(matchesToSearchRequest()::apply).collect(Collectors.toList());
    NodeStorage nodeStorage = tree.getNodeStorage();

    if (filterElements) {
      for (Node savedNode : savedNodes) {
        if (filter.stream().noneMatch(node -> node.equals(savedNode))) {
          if ((filter
              .stream()
              .noneMatch(node -> node.getParent() != null && node.getParent().equals(savedNode)))) {
            nodeStorage.remove(savedNode);
          }
        } else if (getVisibleNodes().stream().noneMatch(node -> node.equals(savedNode))) {
          if (savedNode.getParent() == null) {
            nodeStorage.add(savedNode);
          } else {
            if (getVisibleNodes().isEmpty()) {
              nodeStorage.add(savedNode.getParent());
            }
            nodeStorage.insert(savedNode.getParent(), getIndex(savedNode), savedNode);
          }
        }
      }
    }
    getVisibleNodes().forEach(node -> tree.refresh(node));

    Optional<Node> startsOptional =
        filter
            .stream()
            .filter(
                node ->
                    node.getName().toLowerCase().startsWith(searchRequest.toString().toLowerCase()))
            .findFirst();
    Optional<Node> containsOptional =
        filter
            .stream()
            .filter(
                node ->
                    node.getName().toLowerCase().contains(searchRequest.toString().toLowerCase()))
            .findFirst();

    if (startsOptional.isPresent()) {
      tree.getSelectionModel().select(startsOptional.get(), true);
    } else if (containsOptional.isPresent()) {
      tree.getSelectionModel().select(containsOptional.get(), true);
    } else {
      filter.stream().findFirst().ifPresent(node -> tree.getSelectionModel().select(node, true));
    }
  }

  private int getIndex(Node node) {
    return savedNodes
        .stream()
        .filter(
            savedNode ->
                (getVisibleNodes().stream().anyMatch(savedNode::equals)
                        && (savedNode.getParent() != null
                            && savedNode.getParent().equals(node.getParent())))
                    || savedNode.equals(node))
        .collect(Collectors.toList())
        .indexOf(node);
  }

  private List<Node> getVisibleNodes() {
    List<Node> rootNodes = tree.getRootNodes();
    return tree.getAllChildNodes(rootNodes, true);
  }

  private Predicate<Node> matchesToSearchRequest() {
    return inputNode -> {
      String nodeString = nodeConverter.convert(inputNode);
      return nodeString.toLowerCase().matches(getSearchPattern().toLowerCase());
    };
  }

  private String getSearchPattern() {
    StringBuilder pattern = new StringBuilder(".*");
    for (int i = 0; i < searchRequest.length(); i++) {
      pattern.append(searchRequest.charAt(i)).append(".*");
    }
    return pattern.toString().toLowerCase();
  }

  class SearchRender extends DefaultPresentationRenderer<Node> {
    SearchRender(TreeStyles treeStyles) {
      super(treeStyles);
    }

    @Override
    public Element render(
        final Node node, final String domID, final Tree.Joint joint, final int depth) {
      // Initialize HTML elements.
      final Element rootContainer = super.render(node, domID, joint, depth);
      final Element nodeContainer = rootContainer.getFirstChildElement();

      if (searchRequest.toString().isEmpty()) {
        return rootContainer;
      }

      Element item = nodeContainer.getElementsByTagName("span").getItem(0);
      String innerText = item.getInnerText();

      if (innerText.isEmpty()) {
        item = nodeContainer.getElementsByTagName("div").getItem(0).getFirstChildElement();
        innerText = item.getInnerText();
      }

      List<String> groups = getMatchings(innerText);
      if (groups.isEmpty()) {
        return rootContainer;
      }

      if (!innerText.toLowerCase().matches(getSearchPattern())) {
        return rootContainer;
      }

      item.setInnerText("");

      for (int i = 0; i < groups.size(); i++) {
        String groupValue = groups.get(i);
        SpanElement spanElement1 = (SpanElement) Elements.createSpanElement();
        SpanElement spanElement2 = (SpanElement) Elements.createSpanElement(style);
        spanElement1.setInnerText(
            innerText.substring(0, innerText.toLowerCase().indexOf(groupValue)));
        int index = innerText.toLowerCase().indexOf(groupValue);
        spanElement2.setInnerText(innerText.substring(index, index + groupValue.length()));
        item.appendChild(spanElement1);
        item.appendChild(spanElement2);

        if (i == groups.size() - 1) {
          SpanElement spanElement3 = (SpanElement) Elements.createSpanElement();
          spanElement3.setInnerText(
              innerText.substring(
                  innerText.toLowerCase().indexOf(groupValue) + groupValue.length()));
          item.appendChild(spanElement3);
        } else {
          innerText =
              innerText.substring(
                  innerText.toLowerCase().indexOf(groupValue) + groupValue.length());
        }
      }

      return rootContainer;
    }

    private List<String> getMatchings(String input) {
      String group = "";
      List<String> groups = new ArrayList<>();
      for (int i = 0; i < searchRequest.length(); i++) {

        String value = String.valueOf(searchRequest.charAt(i)).toLowerCase();

        if (input.toLowerCase().contains(group + value)) {
          group += value;
          if (i == searchRequest.length() - 1) {
            groups.add(group);
          }
        } else if (!group.isEmpty()) {
          groups.add(group);
          if (i == searchRequest.length() - 1) {
            groups.add(value);
          } else if (input.toLowerCase().contains(value)) {
            group = value;
          } else {
            group = "";
          }
        }
      }
      return groups;
    }
  }
}
