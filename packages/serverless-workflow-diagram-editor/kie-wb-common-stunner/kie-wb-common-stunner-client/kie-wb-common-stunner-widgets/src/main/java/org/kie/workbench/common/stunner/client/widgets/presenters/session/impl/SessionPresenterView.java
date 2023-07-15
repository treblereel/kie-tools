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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Panel;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

import static org.jboss.errai.common.client.dom.DOMUtil.removeFromParent;

// TODO: i18n.
@Dependent
@Templated
public class SessionPresenterView
        implements SessionPresenter.View {

    protected static final int DELAY = 3000;
    protected static final int NOTIFICATION_LOCK_TIMEOUT = DELAY + 1000;

    @Inject
    @DataField
    private ResizeFlowPanel canvasPanel;

    @Inject
    @DataField
    private SessionContainer sessionContainer;

    private ScrollType scrollType = ScrollType.AUTO;
    private HandlerRegistration handlerRegistration;
    private final AtomicBoolean notifying = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        //handlerRegistration = addDomHandler((e) -> {
        //                                        e.preventDefault();
        //                                        e.stopPropagation();
        //                                    },
        //                                    ContextMenuEvent.getType());

        //addAttachHandler(event -> {
        //    if (event.isAttached()) {
        //        getElement().getParentElement().getStyle().setHeight(100.0, Style.Unit.PCT);
        //        getElement().getParentElement().getStyle().setWidth(100.0, Style.Unit.PCT);
        //    }
        //});

    }

    void onCanvasFocusedSelectionEvent(final @Observes CanvasFocusedShapeEvent event) {
        getSessionContainer().getElement().setScrollLeft(event.getX());
        getSessionContainer().getElement().setScrollTop(event.getY());
    }

    SessionContainer getSessionContainer() {
        return sessionContainer;
    }

    @Override
    public ScrollType getContentScrollType() {
        return scrollType;
    }

    @Override
    public SessionPresenterView setCanvasWidget(final IsElement widget) {
        setWidgetForPanel(canvasPanel,
                          widget);
        return this;
    }

    @Override
    public void setContentScrollType(final ScrollType type) {
        final Style style = sessionContainer.getElement().getStyle();
        switch (type) {
            case AUTO:
                style.setOverflow(Style.Overflow.AUTO);
                break;
            case CUSTOM:
                style.setOverflow(Style.Overflow.HIDDEN);
        }
    }

    @Override
    public SessionPresenterView showError(final String message) {
        return this;
    }

    @Override
    public SessionPresenter.View showWarning(final String message) {
        return this;
    }

    //show only one notification at a time
    private void singleNotify(final Runnable notification) {
        //check if any other notification is ongoing and set a lock
        if (notifying.compareAndSet(false, true)) {
            //timer to remove the lock on notification
            new Timer() {
                @Override
                public void run() {
                    notifying.set(false);
                }
            }.schedule(NOTIFICATION_LOCK_TIMEOUT);

            notification.run();
        }
    }

    @Override
    public SessionPresenterView showMessage(final String message) {
        return this;
    }

    @Override
    public void onResize() {
        canvasPanel.onResize();
    }

    protected void setWidgetForPanel(final Panel panel,
                                     final IsElement widget) {
        panel.clear();
        panel.getElement().appendChild(Js.uncheckedCast(widget.getElement())); //TODO
    }

    public HTMLElement getElement() {
        return Js.uncheckedCast(sessionContainer.getElement());
    }

    public void destroy() {
        handlerRegistration.removeHandler();
        handlerRegistration = null;
        canvasPanel.clear();
        canvasPanel.removeFromParent();
        sessionContainer.clear();
        sessionContainer.removeFromParent();
        removeFromParent(getElement());
    }

}
