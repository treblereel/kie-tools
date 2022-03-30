/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.widgets.common.client.common;

import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.event.logical.shared.OpenEvent;
import org.gwtproject.event.logical.shared.OpenHandler;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.uibinder.client.UiBinder;
import org.gwtproject.uibinder.client.UiField;
import org.gwtproject.user.client.ui.HorizontalPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.resources.CommonImages;

public class LazyStackPanelHeader extends AbstractLazyStackPanelHeader {

    private static LazyStackPanelHeaderBinder uiBinder = null;
    @UiField
    Image icon;
    @UiField
    HorizontalPanel container;
    private ClickHandler expandClickHandler = new ClickHandler() {

        public void onClick(ClickEvent event) {
            onTitleClicked();
        }
    };

    public LazyStackPanelHeader(final String headerText,
                                final ImageResource headerIcon) {
        this(headerText,
             new Image(headerIcon));
    }

    public LazyStackPanelHeader(final String headerText,
                                final Image headerIcon) {
        this();
        final Image titleIcon = headerIcon;
        container.add(titleIcon);
        final Label titleLabel = new Label(headerText);
        titleLabel.setStyleName("guvnor-cursor");
        titleLabel.addClickHandler(expandClickHandler);
        container.add(titleLabel);
    }

    public LazyStackPanelHeader(String headerText) {
        this();
        final Label titleLabel = new Label(headerText);
        titleLabel.setStyleName("guvnor-cursor");
        titleLabel.addClickHandler(expandClickHandler);
        container.add(titleLabel);
    }

    public LazyStackPanelHeader() {

        add(uiBinder.createAndBindUi(this));

        icon.addClickHandler(expandClickHandler);

        setIconImage();

        addOpenHandler(new OpenHandler<AbstractLazyStackPanelHeader>() {
            public void onOpen(OpenEvent<AbstractLazyStackPanelHeader> event) {
                expanded = true;
                setIconImage();
            }
        });

        addCloseHandler(new CloseHandler<AbstractLazyStackPanelHeader>() {
            public void onClose(CloseEvent<AbstractLazyStackPanelHeader> event) {
                expanded = false;
                setIconImage();
            }
        });
    }

    public void expand() {
        if (!expanded) {
            onTitleClicked();
        }
    }

    public void collapse() {
        if (expanded) {
            onTitleClicked();
        }
    }

    private void setIconImage() {
        if (expanded) {
            icon.setResource(CommonImages.INSTANCE.collapse());
        } else {
            icon.setResource(CommonImages.INSTANCE.expand());
        }
    }

    private void onTitleClicked() {
        if (expanded) {
            CloseEvent.fire(this,
                            this);
        } else {
            OpenEvent.fire(this,
                           this);
        }
    }

    interface LazyStackPanelHeaderBinder
            extends
            UiBinder<Widget, LazyStackPanelHeader> {

    }
}
