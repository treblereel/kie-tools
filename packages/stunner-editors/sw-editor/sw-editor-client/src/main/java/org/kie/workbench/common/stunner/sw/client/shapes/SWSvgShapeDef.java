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

package org.kie.workbench.common.stunner.sw.client.shapes;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.TitleHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.sw.client.resources.SWSVGViewFactory;

public interface SWSvgShapeDef<W>
        extends ShapeViewDef<W, SVGShapeView>,
                SVGShapeViewDef<W, SWSVGViewFactory> {

    @Override
    default Optional<BiConsumer<String, SVGShapeView>> titleHandler() {
        return Optional.of(newTitleHandler()::handle);
    }

    @Override
    @SuppressWarnings("unchecked")
    default Optional<BiConsumer<W, SVGShapeView>> fontHandler() {
        return Optional.of(newFontHandler()::handle);
    }

    @Override
    default BiConsumer<W, SVGShapeView> viewHandler() {
        return newViewAttributesHandler()::handle;
    }

    default TitleHandler<ShapeView> newTitleHandler() {
        return StateViewHandlers.TITLE_HANDLER;
    }

    default StateViewHandlers.FontHandlerBuilder<W, SVGShapeView> newFontHandlerBuilder() {
        return new StateViewHandlers.FontHandlerBuilder<>();
    }

    default StateViewHandlers.ViewAttributesHandlerBuilder<W, SVGShapeView> newViewAttributesHandlerBuilder() {
        return new StateViewHandlers.ViewAttributesHandlerBuilder<W, SVGShapeView>();
    }

    default FontHandler<W, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder().build();
    }

    default ViewAttributesHandler<W, SVGShapeView> newViewAttributesHandler() {
        return newViewAttributesHandlerBuilder().build();
    }

    default Class<SWSVGViewFactory> getViewFactoryType() {
        return SWSVGViewFactory.class;
    }
}
