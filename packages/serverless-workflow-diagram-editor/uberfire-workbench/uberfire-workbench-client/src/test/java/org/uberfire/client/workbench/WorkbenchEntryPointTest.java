/*
 *
 *  * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.workbench;

import java.lang.annotation.Annotation;
import java.util.Collections;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.util.MockIOCBeanDef;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(DockLayoutPanel.class)
public class WorkbenchEntryPointTest {

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private FlowPanel dockContainer;

    @Mock
    private Activity dockActivity;

    @Mock
    private SimpleLayoutPanel dockPanel;

    @Spy
    @InjectMocks
    private WorkbenchEntryPoint workbenchEntryPoint;

    private static final String DOCK_ID = "DockTest";
    private static final PlaceRequest DOCK_PLACE = new DefaultPlaceRequest(DOCK_ID);


    @SuppressWarnings("unchecked")
    private <T> void makeDockBean(final T beanInstance,
                                  final Class<? extends Annotation> scope) {
        final Class<T> type = (Class<T>) Activity.class;
        final SyncBeanDef<T> beanDef = new MockIOCBeanDef<>(beanInstance,
                                                            type,
                                                            scope,
                                                            null,
                                                            DOCK_ID,
                                                            true);
        when(iocManager.lookupBeans(type)).thenReturn(Collections.singletonList(beanDef));
    }
}
