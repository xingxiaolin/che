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
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.che.ide.DelayedTask;
import org.eclipse.che.ide.ui.smartTree.converter.NodeConverter;
import org.eclipse.che.ide.ui.smartTree.converter.impl.NodeNameConverter;
import org.eclipse.che.ide.ui.smartTree.data.Node;
import org.eclipse.che.ide.ui.smartTree.presentation.DefaultPresentationRenderer;
import org.eclipse.che.ide.util.dom.Elements;

import static com.google.gwt.dom.client.Style.BorderStyle.SOLID;
import static com.google.gwt.dom.client.Style.Position.FIXED;
import static com.google.gwt.dom.client.Style.Unit.PX;

/** @author Vlad Zhukovskiy */
public class SpeedSearch {

  private Tree tree;
  private NodeConverter<Node, String> nodeConverter;
  private DelayedTask searchTask;
  private StringBuilder searchRequest;
  private SearchPopUp searchPopUp;
  private static final String INITIAL_SEARCH_TEXT = "Search for: ";
  private static final String ID = "speedSearch";

  private int searchDelay;
  private List<Node> savedNodes;
  private List<Node> rootItems;

  private class SearchPopUp extends SimplePanel {
    private Label searchLabel;

    private SearchPopUp() {
      getElement().setId(ID);
      setVisible(false); // by default
      this.searchLabel = new Label(INITIAL_SEARCH_TEXT);

      add(searchLabel);
    }

    private void setSearchRequest(String request) {
      searchLabel.setText(INITIAL_SEARCH_TEXT + request);
    }
  }

  private KeyboardNavigationHandler keyNav =
      new KeyboardNavigationHandler() {

        @Override
        public void onBackspace(NativeEvent evt) {
          evt.preventDefault();
          if (!Strings.isNullOrEmpty(searchRequest.toString())) {
            searchRequest.setLength(searchRequest.length() - 1);
            doSearch();
          }
        }

        @Override
        public void onUp(NativeEvent evt) {
          // check if we have found nodes that matches search pattern and navigate to previous node
          // by pressing Up key
        }

        @Override
        public void onDown(NativeEvent evt) {
          // check if we have found nodes that matches search pattern and navigate to previous node
          // by pressing Down key
        }

        @Override
        public void onEnd(NativeEvent evt) {
          // iterate to last found node
        }

        @Override
        public void onHome(NativeEvent evt) {
          // iterate to first found node
        }

        @Override
        public void onEsc(NativeEvent evt) {
          removeSearchPopUpFromTree();
          // clear search pattern and restore tree to normal mode
        }

        @Override
        public void onEnter(NativeEvent evt) {
          removeSearchPopUpFromTree();
          // handle enter key, for leaf node we should check whether node is implemented by
          // HasAction interface
          // and fire action performed, otherwise for non-leaf node we should expand/collapse node
        }

        @Override
        public void onKeyPress(NativeEvent evt) {
          char sChar = (char) evt.getKeyCode();

          if (Character.isLetterOrDigit(sChar)) {
            //                evt.preventDefault(); //not sure if this right decision
            evt.stopPropagation();
            searchRequest.append(sChar);
            update();
          }
          // gather key press and try to search through visible nodes to find nodes that matches
          // search pattern
        }
      };

  public SpeedSearch(Tree tree, NodeConverter<Node, String> nodeConverter) {
    this.tree = tree;
    this.tree.setPresentationRenderer(new SearchRender(tree.getTreeStyles()));
    this.nodeConverter = nodeConverter != null ? nodeConverter : new NodeNameConverter();

    keyNav.bind(tree);

    this.searchDelay = 100; // 100ms
    this.searchRequest = new StringBuilder();
    initSearchPopUp();
  }

  private void initSearchPopUp() {
    this.searchPopUp = new SearchPopUp();
    Style style = this.searchPopUp.getElement().getStyle();

    style.setBackgroundColor("grey");
    style.setBorderStyle(SOLID);
    style.setBorderColor("#dbdbdb");
    style.setBorderWidth(1, PX);
    style.setPadding(2, PX);
    style.setPosition(FIXED);
    style.setTop(1, PX);
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

  private void doSearch() {
    if (Strings.isNullOrEmpty(searchRequest.toString())) {
      cancelSearch();
      return;
    }

    addSearchPopUpToTree();
    searchPopUp.setSearchRequest(searchRequest.toString());
    tree.getSelectionModel().deselectAll();

    savedNodes = savedNodes == null ? getVisibleNodes() : savedNodes;
    rootItems = rootItems == null ? tree.getRootNodes() : rootItems;

    List<Node> filter =
        savedNodes.stream().filter(matchesToSearchRequest()::apply).collect(Collectors.toList());

    boolean first = false;
    NodeStorage nodeStorage = tree.getNodeStorage();

    for (Node savedNode : savedNodes) {
      if (filter.stream().noneMatch(node -> equals(node, savedNode))) {
        if ((filter
            .stream()
            .noneMatch(node -> node.getParent() != null && equals(node.getParent(), savedNode)))) {
          getVisibleNodes()
              .stream()
              .filter(node -> equals(node, savedNode))
              .findFirst()
              .ifPresent(nodeStorage::remove);
        }
      } else if (getVisibleNodes().stream().noneMatch(node -> equals(node, savedNode))) {
        nodeStorage.insert(savedNode.getParent(), getIndex(savedNode), savedNode);
      }
    }

    for (Node filteredNode : filter) {
      if (!first) {
        tree.scrollIntoView(filteredNode);
        first = true;
      }
      tree.getSelectionModel().select(filteredNode, true);
    }
    getVisibleNodes().forEach(node -> tree.refresh(node));
  }

  private boolean equals(Node node1, Node node2) {
    return node1.getName().equals(node2.getName());
  }

  private int getIndex(Node name) {
    List<String> collect =
        tree.getNodeStorage()
            .getChildren(name.getParent())
            .stream()
            .map(Node::getName)
            .collect(Collectors.toList());
    collect.add(name.getName());
    collect.sort(String.CASE_INSENSITIVE_ORDER);
    return collect.indexOf(name.getName());
  }

  private void cancelSearch() {
    removeSearchPopUpFromTree();
    tree.getNodeStorage().clear();
    tree.getNodeStorage().add(rootItems);
    tree.expandAll();
    Node node = tree.getRootNodes().get(0);
    tree.getSelectionModel().select(node, false);
    tree.scrollIntoView(node);
  }

  private List<Node> getVisibleNodes() {
    List<Node> rootNodes = tree.getRootNodes();
    return tree.getAllChildNodes(rootNodes, true);
  }

  private Predicate<Node> matchesToSearchRequest() {

    StringBuilder pattern = new StringBuilder(".*");
    for (int i = 0; i < searchRequest.length(); i++) {
      pattern.append(searchRequest.charAt(i)).append(".*");
    }

    return inputNode -> {
      String nodeString = nodeConverter.convert(inputNode);
      return nodeString.toLowerCase().matches(pattern.toString().toLowerCase());
    };
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

      String group = "";
      List<String> groups = new ArrayList<>();
      for (int i = 0; i < searchRequest.length(); i++) {

        String value = String.valueOf(searchRequest.charAt(i)).toLowerCase();

        if (innerText.toLowerCase().contains(group + value)) {
          group += value;
          if (i == searchRequest.length() - 1) {
            groups.add(group);
          }



        } else if (!group.isEmpty()) {
          groups.add(group);
          if (i == searchRequest.length() - 1) {
            groups.add(value);
          } else if (innerText.toLowerCase().contains(value)) {
            group = value;
          } else {
            group = "";
          }
        }
      }

      if (groups.isEmpty()) {
        return rootContainer;
      }

      item.setInnerText("");

      for (String groupValue : groups) {
        SpanElement spanElement1 = (SpanElement) Elements.createSpanElement();
        SpanElement spanElement2 = (SpanElement) Elements.createSpanElement();
        spanElement1.setInnerText(
            innerText.substring(0, innerText.toLowerCase().indexOf(groupValue)));
        int i = innerText.toLowerCase().indexOf(groupValue);
        spanElement2.setInnerText(innerText.substring(i, i + groupValue.length()));
        spanElement2.getStyle().setColor("red");
        item.appendChild(spanElement1);
        item.appendChild(spanElement2);

        if (groups.indexOf(groupValue) == groups.size() - 1) {
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
  }
}
