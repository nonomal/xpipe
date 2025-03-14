package io.xpipe.app.comp.augment;

import io.xpipe.app.comp.CompStructure;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class GrowAugment<S extends CompStructure<?>> implements Augment<S> {

    private final boolean width;
    private final boolean height;

    private GrowAugment(boolean width, boolean height) {
        this.width = width;
        this.height = height;
    }

    public static <S extends CompStructure<?>> GrowAugment<S> create(boolean width, boolean height) {
        return new GrowAugment<>(width, height);
    }

    private void bind(Region r, Node parent) {
        if (!(parent instanceof Region p)) {
            return;
        }

        if (width) {
            r.prefWidthProperty()
                    .bind(Bindings.createDoubleBinding(
                            () -> {
                                var val = p.getWidth()
                                        - p.getInsets().getLeft()
                                        - p.getInsets().getRight();
                                if (val <= 0) {
                                    return Region.USE_COMPUTED_SIZE;
                                }

                                // Floor to prevent rounding issues which cause an infinite growing
                                return Math.floor(val);
                            },
                            p.widthProperty(),
                            p.insetsProperty()));
        }
        if (height) {
            r.prefHeightProperty()
                    .bind(Bindings.createDoubleBinding(
                            () -> {
                                var val = p.getHeight()
                                        - p.getInsets().getTop()
                                        - p.getInsets().getBottom();
                                if (val <= 0) {
                                    return Region.USE_COMPUTED_SIZE;
                                }

                                // Floor to prevent rounding issues which cause an infinite growing
                                return Math.floor(val);
                            },
                            p.heightProperty(),
                            p.insetsProperty()));
        }
    }

    @Override
    public void augment(S struc) {
        struc.get().parentProperty().addListener((c, o, n) -> {
            if (o instanceof Region) {
                if (width) {
                    struc.get().prefWidthProperty().unbind();
                }
                if (height) {
                    struc.get().prefHeightProperty().unbind();
                }
            }

            bind(struc.get(), n);
        });

        bind(struc.get(), struc.get().getParent());
    }
}
