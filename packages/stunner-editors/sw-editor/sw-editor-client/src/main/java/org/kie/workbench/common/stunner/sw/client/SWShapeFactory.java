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

package org.kie.workbench.common.stunner.sw.client;

import java.util.Optional;
import java.util.function.BiConsumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.resources.client.ImageResource;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.TitleHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.shapes.def.CircleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;
import org.kie.workbench.common.stunner.sw.client.resources.SWImageResources;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;

import static org.kie.workbench.common.stunner.core.util.ClassUtils.getName;

@ApplicationScoped
public class SWShapeFactory
        implements ShapeFactory<Object, Shape> {

    private final BasicShapesFactory basicShapesFactory;

    // CDI proxy.
    protected SWShapeFactory() {
        this.basicShapesFactory = null;
    }

    @Inject
    public SWShapeFactory(final BasicShapesFactory basicShapesFactory) {
        this.basicShapesFactory = basicShapesFactory;
    }

    @Override
    @SuppressWarnings("all")
    public Shape newShape(final Object definition) {

        if ((definition instanceof Start) ||
                (definition instanceof End)) {
            return newEventShape(definition);
        }

        if (definition instanceof InjectState) {
            return newStateShape(definition, "#FF0000");
        }

        if (definition instanceof SwitchState) {
            return newStateShape(definition, "#00FF00");
        }

        if (definition instanceof State) {
            return newStateShape(definition, "#FFFFFF");
        }

        if (definition instanceof Transition) {
            return newConnectorShape(definition, "#000000");
        }

        if (definition instanceof StartTransition) {
            return newConnectorShape(definition, "#0000FF");
        }

        if (definition instanceof ErrorTransition) {
            return newConnectorShape(definition, "#FF0000");
        }

        throw new UnsupportedOperationException("No shape declared in SW factory.");
    }

    @Override
    @SuppressWarnings("all")
    public Glyph getGlyph(final String definitionId) {

        ImageResource imageResource = null;

        if (getName(Start.class).equals(definitionId)) {
            imageResource = SWImageResources.INSTANCE.start();
        }

        if (getName(End.class).equals(definitionId)) {
            imageResource = SWImageResources.INSTANCE.end();
        }

        if (getName(InjectState.class).equals(definitionId)) {
            imageResource = SWImageResources.INSTANCE.stateInject();
        }

        if (getName(SwitchState.class).equals(definitionId)) {
            imageResource = SWImageResources.INSTANCE.stateSwitch();
        }

        if (getName(Transition.class).equals(definitionId)) {
            imageResource = SWImageResources.INSTANCE.transition();
        }

        if (getName(StartTransition.class).equals(definitionId)) {
            imageResource = SWImageResources.INSTANCE.transitionStart();
        }

        if (getName(ErrorTransition.class).equals(definitionId)) {
            imageResource = SWImageResources.INSTANCE.transitionError();
        }

        if (null != imageResource) {
            return ImageDataUriGlyph.create(imageResource.getSafeUri());
        }

        throw new UnsupportedOperationException("No glyph declared in SW factory.");
    }

    private Shape newConnectorShape(final Object bean, String color) {
        return basicShapesFactory.newShape(bean, new EdgeShapeDef(color));
    }

    private Shape newEventShape(final Object bean) {
        return basicShapesFactory.newShape(bean, new EventShapeDef());
    }

    private Shape newStateShape(final Object state, String color) {
        return basicShapesFactory.newShape(state, new StageShapeDef(color));
    }

    private static class EdgeShapeDef implements ConnectorShapeDef {

        private final String color;

        private EdgeShapeDef(String color) {
            this.color = color;
        }

        @Override
        public BiConsumer viewHandler() {
            ViewAttributesHandler handler = new ViewAttributesHandlerBuilder(color, color).build();
            return (o, o2) -> handler.handle(o, (ShapeView) o2);
        }
    }

    private static class EventShapeDef implements CircleShapeDef {

        @Override
        public Double getRadius(Object element) {
            return 12.5d;
        }

        @Override
        public BiConsumer viewHandler() {
            ViewAttributesHandler handler = new ViewAttributesHandlerBuilder("#000000", "#FFFFFF").build();
            return (o, o2) -> handler.handle(o, (ShapeView) o2);
        }
    }

    private static class StageShapeDef implements RectangleShapeDef {

        private final String color;

        private StageShapeDef(String color) {
            this.color = color;
        }

        @Override
        public Double getWidth(Object element) {
            return 100d;
        }

        @Override
        public Double getHeight(Object element) {
            return 50d;
        }

        @Override
        public double getCornerRadius(Object element) {
            return 5d;
        }

        @Override
        @SuppressWarnings("all")
        public Optional<BiConsumer> titleHandler() {
            TitleHandler<ShapeView> handler = new TitleHandler<>();
            return Optional.of((o, o2) -> handler.handle((String) o, (ShapeView) o2));
        }

        @Override
        @SuppressWarnings("all")
        public Optional<BiConsumer> fontHandler() {
            FontHandler<Object, ShapeView> handler = new FontHandler.Builder<Object, ShapeView>()
                    .horizontalAlignment(o -> HasTitle.HorizontalAlignment.LEFT)
                    //.verticalAlignment(o -> HasTitle.VerticalAlignment.TOP)
                    .build();
            return Optional.of((o, o2) -> handler.handle(o, (ShapeView) o2));
        }

        @Override
        @SuppressWarnings("all")
        public BiConsumer viewHandler() {
            ViewAttributesHandler handler = new ViewAttributesHandlerBuilder(color, "#FFFFFF").build();
            return (o, o2) -> handler.handle(o, (ShapeView) o2);
        }
    }

    private static class ViewAttributesHandlerBuilder<W extends Object, V extends ShapeView>
            extends ViewAttributesHandler.Builder<W, V> {

        public ViewAttributesHandlerBuilder(String fillColor, String strokeColor) {
            this.fillColor(bean -> fillColor)
                    .strokeColor(bean -> strokeColor)
                    .strokeWidth(bean -> 1d);
        }
    }
}
