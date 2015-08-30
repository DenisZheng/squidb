/*
 * Copyright 2015, Yahoo Inc.
 * Copyrights licensed under the Apache 2.0 License.
 * See the accompanying LICENSE file for terms.
 */
package com.yahoo.squidb.processor.plugins.defaults.properties;

import com.yahoo.aptutils.model.DeclaredTypeName;
import com.yahoo.aptutils.utils.AptUtils;
import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.processor.TypeConstants;
import com.yahoo.squidb.processor.data.InheritedModelSpecWrapper;
import com.yahoo.squidb.processor.data.ModelSpec;
import com.yahoo.squidb.processor.plugins.defaults.properties.generators.PropertyGenerator;

import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

/**
 * This plugin controls generating property declarations, getters, and setters for fields in an inherited model. It can
 * create instances of {@link PropertyGenerator} for references to other Property subclasses (StringProperty,
 * LongProperty, etc.)
 */
public class InheritedModelSpecFieldPlugin extends FieldReferencePlugin {

    public InheritedModelSpecFieldPlugin(ModelSpec<?> modelSpec, AptUtils utils) {
        super(modelSpec, utils);
    }

    @Override
    public boolean hasChangesForModelSpec() {
        return modelSpec instanceof InheritedModelSpecWrapper;
    }

    @Override
    public boolean processVariableElement(VariableElement field, DeclaredTypeName fieldType) {
        if (field.getAnnotation(Deprecated.class) != null) {
            return false;
        }
        if (field.getAnnotation(ColumnSpec.class) != null) {
            utils.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "ColumnSpec is ignored outside of table models", field);
        }
        Set<Modifier> modifiers = field.getModifiers();
        if (modifiers.containsAll(TypeConstants.PUBLIC_STATIC_FINAL) && TypeConstants.isPropertyType(fieldType)) {
            return tryCreatePropertyGenerator(field, fieldType);
        }
        return false;
    }
}
