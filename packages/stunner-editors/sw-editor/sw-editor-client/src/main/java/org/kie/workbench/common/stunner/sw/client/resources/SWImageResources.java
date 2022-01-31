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

package org.kie.workbench.common.stunner.sw.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;

public interface SWImageResources extends ClientBundleWithLookup {

    SWImageResources INSTANCE = GWT.create(SWImageResources.class);

    @Source("images/start.png")
    ImageResource start();

    @Source("images/end.png")
    ImageResource end();

    @Source("images/state_inject.png")
    ImageResource stateInject();

    @Source("images/transition.png")
    ImageResource transition();

    @Source("images/transition_start.png")
    ImageResource transitionStart();

    @Source("images/transition_error.png")
    ImageResource transitionError();
}
