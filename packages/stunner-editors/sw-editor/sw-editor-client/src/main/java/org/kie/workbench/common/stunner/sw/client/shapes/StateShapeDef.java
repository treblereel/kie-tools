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

package org.kie.workbench.common.stunner.sw.client.shapes;

import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.HorizontalAlignment;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.sw.client.resources.SWGlyphFactory;
import org.kie.workbench.common.stunner.sw.client.resources.SWSVGViewFactory;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;

public class StateShapeDef
        implements SWSvgShapeDef<State> {

    public static final double ICON_WIDTH = 35d;

    public static final SVGShapeViewResources<State, SWSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<State, SWSVGViewFactory>()
                    .put(InjectState.class, SWSVGViewFactory::injectState)
                    .put(SwitchState.class, SWSVGViewFactory::switchState);
    //.put(OperationState.class, SWSVGViewFactory::operationState)

    public static final Map<Class<? extends State>, Glyph> GLYPHS =
            new Maps.Builder<Class<? extends State>, Glyph>()
                    .put(InjectState.class, SWGlyphFactory.STATE_INJECT)
                    .put(SwitchState.class, SWGlyphFactory.STATE_SWITCH)
                    //.put(OperationState.class, SWGlyphFactory.STATE_OPERATION)
                    .build();

    private static final Map<Enum, Double> DEFAULT_TASK_MARGINS_WITH_ICON =
            new Maps.Builder<Enum, Double>()
                    .put(HorizontalAlignment.LEFT, ICON_WIDTH)
                    .build();

    private static Map<Class<? extends State>, Map<Enum, Double>> taskMarginSuppliers =
            new Maps.Builder()
                    .put(State.class, null)
                    .put(InjectState.class, DEFAULT_TASK_MARGINS_WITH_ICON)
                    .put(SwitchState.class, DEFAULT_TASK_MARGINS_WITH_ICON)
                    //.put(OperationState.class, DEFAULT_TASK_MARGINS_WITH_ICON)
                    .build();

    @Override
    public BiConsumer<State, SVGShapeView> viewHandler() {
        return newViewAttributesHandler()::handle;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final SWSVGViewFactory factory,
                                           final State state) {
        return VIEW_RESOURCES.getResource(factory, state).build(false);
    }

    @Override
    public Glyph getGlyph(final Class<? extends State> type,
                          final String defId) {
        return GLYPHS.get(type);
    }

    @Override
    public FontHandler<State, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .margins(bean -> taskMarginSuppliers.getOrDefault(bean.getClass(), null))
                .build();
    }
}