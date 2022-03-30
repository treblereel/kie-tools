/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.popups;

import elemental2.dom.HTMLElement;
import io.crysknife.ui.databinding.client.ElementWrapperWidget;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtproject.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

public class CommonModalBuilder {

    private final BaseModal modal;

    public CommonModalBuilder() {
        modal = new BaseModal();
    }

    public CommonModalBuilder addHeader(String title) {
        this.getModal().setTitle(title);
        return this;
    }

    public CommonModalBuilder addBody(HTMLElement element) {
        this.getModal().add(buildPanel(element,
                             makeModalBody()));
        return this;
    }

    public CommonModalBuilder addFooter(ModalFooter footer) {
        this.getModal().add(footer);
        return this;
    }

    public CommonModalBuilder addFooter(final HTMLElement htmlElement) {

        final FlowPanel flowPanel = buildPanel(htmlElement,
                                               makeModalFooter());
        getModal().add(flowPanel);
        return this;
    }

    public BaseModal build() {
        return getModal();
    }

    protected BaseModal getModal() {
        return modal;
    }

    ModalBody makeModalBody() {
        return new ModalBody();
    }

    ModalFooter makeModalFooter() {
        return new ModalFooter();
    }

    protected FlowPanel buildPanel(HTMLElement element,
                                   FlowPanel panel) {
        panel.add(build(element));
        return panel;
    }

    private Widget build(HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }

}
