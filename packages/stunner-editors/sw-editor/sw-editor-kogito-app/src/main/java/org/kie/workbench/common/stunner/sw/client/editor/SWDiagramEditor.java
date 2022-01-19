/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.sw.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.JsCanvas;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.util.WindowJSType;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.sw.client.services.SWClientDiagramService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.bridge.Notification;

@ApplicationScoped
public class SWDiagramEditor {

    public static final String EDITOR_ID = "SWDiagramEditor";

    private final Promises promises;
    private final StunnerEditor stunnerEditor;
    private final SWClientDiagramService diagramService;

    @Inject
    public SWDiagramEditor(Promises promises,
                           StunnerEditor stunnerEditor,
                           SWClientDiagramService diagramService) {
        this.promises = promises;
        this.stunnerEditor = stunnerEditor;
        this.diagramService = diagramService;
    }

    public void onStartup(final PlaceRequest place) {
        stunnerEditor.setReadOnly(true);
    }

    public void onOpen() {
    }

    public void onClose() {
        close();
    }

    private void close() {
        stunnerEditor.close();
    }

    public IsWidget asWidget() {
        return stunnerEditor.getView();
    }

    public Promise<String> getContent() {
        return promises.resolve("{}");
    }

    public Promise<String> getPreview() {
        return promises.resolve("");
    }

    public Promise validate() {
        return Promise.resolve(new Notification[0]);
    }

    public Promise<Void> setContent(final String path, final String value) {
        close();
        return promises.create((success, failure) -> {
            diagramService.transform(path,
                                     value,
                                     new ServiceCallback<Diagram>() {

                                         @Override
                                         public void onSuccess(final Diagram diagram) {
                                             stunnerEditor
                                                     .close()
                                                     .open(diagram, new SessionPresenter.SessionPresenterCallback() {
                                                         @Override
                                                         public void onSuccess() {
                                                             onDiagramOpenSuccess();
                                                             success.onInvoke((Void) null);
                                                         }

                                                         @Override
                                                         public void onError(ClientRuntimeError error) {
                                                             failure.onInvoke(error);
                                                         }
                                                     });
                                         }

                                         @Override
                                         public void onError(final ClientRuntimeError error) {
                                             stunnerEditor.handleError(error);
                                             failure.onInvoke(error);
                                         }
                                     });
        });
    }

    private void onDiagramOpenSuccess() {
        Diagram diagram = stunnerEditor.getCanvasHandler().getDiagram();
        Metadata metadata = diagram.getMetadata();
        String title = metadata.getTitle();
        Path path = PathFactory.newPath(title, "/" + title + ".sw");
        metadata.setPath(path);
        initLienzoType();
    }

    private void initLienzoType() {
        LienzoCanvas canvas = (LienzoCanvas) stunnerEditor.getCanvasHandler().getCanvas();
        if (canvas != null) {
            LienzoPanel panel = (LienzoPanel) canvas.getView().getPanel();
            LienzoBoundsPanel lienzoPanel = panel.getView();
            JsCanvas jsCanvas = new JsCanvas(lienzoPanel, lienzoPanel.getLayer());
            setupJsCanvasTypeNative(jsCanvas);
        }
    }

    private static void setupJsCanvasTypeNative(JsCanvas jsCanvas) {
        WindowJSType.linkCanvasJS(jsCanvas);
    }
}
