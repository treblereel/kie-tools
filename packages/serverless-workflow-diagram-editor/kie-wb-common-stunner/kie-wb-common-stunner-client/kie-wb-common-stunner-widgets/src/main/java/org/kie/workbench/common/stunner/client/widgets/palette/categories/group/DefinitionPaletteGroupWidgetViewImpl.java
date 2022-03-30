/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.categories.group;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Event;
import elemental2.dom.HTMLLIElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;

@Templated
@Dependent
public class DefinitionPaletteGroupWidgetViewImpl implements DefinitionPaletteGroupWidgetView,
        IsElement {

    @Inject
    @DataField
    private HTMLLIElement moreAnchor;

    @Inject
    @DataField
    private HTMLLIElement lessAnchor;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initView() {
        DOMUtil.removeAllChildren(this.getElement());
    }

    @Override
    public void addItem(DefinitionPaletteItemWidget categoryItem) {

        PortablePreconditions.checkNotNull("categoryItem",
                categoryItem);
        this.getElement().appendChild(categoryItem.getElement());
    }

    @Override
    public void addAnchors() {
        moreAnchor.style.setProperty("display",
                "none");
        lessAnchor.style.setProperty("display",
                "none");
        this.getElement().appendChild(moreAnchor);
        this.getElement().appendChild(lessAnchor);
    }

    @Override
    public void showMoreAnchor() {
        moreAnchor.style.setProperty("display",
                "block");
        lessAnchor.style.setProperty("display",
                "none");
    }

    @Override
    public void showLessAnchor() {
        moreAnchor.style.setProperty("display",
                "none");
        lessAnchor.style.setProperty("display",
                "block");
    }

    @EventHandler("moreAnchor")
    public void showMore(@ForEvent("click") Event clickEvent) {
        presenter.showMore();
    }

    @EventHandler("lessAnchor")
    public void showLess(@ForEvent("click") Event  clickEvent) {
        presenter.showLess();
    }

    @PreDestroy
    public void destroy() {
        DOMUtil.removeAllChildren(moreAnchor);
        DOMUtil.removeAllChildren(lessAnchor);
        presenter = null;
    }
}
