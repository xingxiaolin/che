/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
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
import com.google.common.collect.Iterables;
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import java.util.List;
import org.eclipse.che.ide.DelayedTask;
import org.eclipse.che.ide.ui.smartTree.converter.NodeConverter;
import org.eclipse.che.ide.ui.smartTree.converter.impl.NodeNameConverter;
import org.eclipse.che.ide.ui.smartTree.data.Node;
import org.eclipse.che.ide.ui.smartTree.presentation.DefaultPresentationRenderer;
import org.eclipse.che.ide.util.dom.Elements;

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

  private class SearchPopUp extends SimplePanel {
    private Label searchLabel;

    public SearchPopUp() {
      getElement().setId(ID);
      setVisible(false); // by default
      this.searchLabel = new Label(INITIAL_SEARCH_TEXT);

      add(searchLabel);
    }

    public void setSearchRequest(String request) {
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
    style.setBorderStyle(Style.BorderStyle.SOLID);
    style.setBorderColor("#dbdbdb");
    style.setBorderWidth(1, Style.Unit.PX);
    style.setPadding(2, Style.Unit.PX);
    style.setPosition(Style.Position.FIXED);
    style.setTop(1, Style.Unit.PX);
    style.setLeft(20, Style.Unit.PX);
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

    Iterable<Node> filter = Iterables.filter(getVisibleNodes(), matchesToSearchRequest());

    boolean first = false;
    for (Node node : filter) {
      if (!first) {
        tree.scrollIntoView(node);
        first = true;
      }
      tree.getSelectionModel().select(node, true);
      tree.refresh(node);
    }
  }

  private void cancelSearch() {
    removeSearchPopUpFromTree();
    Node node = tree.getRootNodes().get(0);
    tree.getSelectionModel().select(node, false);
    tree.scrollIntoView(node);
  }

  private List<Node> getVisibleNodes() {
    List<Node> rootNodes = tree.getRootNodes();
    return tree.getAllChildNodes(rootNodes, true);
  }

  private Predicate<Node> matchesToSearchRequest() {
    return new Predicate<Node>() {
      @Override
      public boolean apply(Node inputNode) {
        String nodeString = nodeConverter.convert(inputNode);
        return nodeString.toLowerCase().contains(searchRequest.toString().toLowerCase());
      }
    };
  }

  class SearchRender extends DefaultPresentationRenderer<Node> {
    public SearchRender(TreeStyles treeStyles) {
      super(treeStyles);
    }

    @Override
    public Element render(
        final Node node, final String domID, final Tree.Joint joint, final int depth) {
      // Initialize HTML elements.
      final Element rootContainer = super.render(node, domID, joint, depth);
      final Element nodeContainer = rootContainer.getFirstChildElement();
      Element item = nodeContainer.getElementsByTagName("div").getItem(0).getFirstChildElement();
      String innerText = item.getInnerText();

      item.removeAllChildren();

      SpanElement spanElement1 = (SpanElement) Elements.createSpanElement();
      SpanElement spanElement2 = (SpanElement) Elements.createSpanElement();
      SpanElement spanElement3 = (SpanElement) Elements.createSpanElement();
      spanElement1.setInnerText(
          innerText.substring(0, innerText.indexOf(searchRequest.toString())));
      spanElement2.setInnerText(searchRequest.toString());
      spanElement3.setInnerText(innerText.substring(innerText.indexOf(searchRequest.toString())));
      item.appendChild(spanElement1);
      item.appendChild(spanElement2);
      item.appendChild(spanElement3);

      return rootContainer;
    }
  }
}
