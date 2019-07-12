package us.tlatoani.tablisknu.blueprint;

import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;

public class ExprBlueprintCopy extends MundoPropertyExpression<Blueprint, Blueprint> {
    @Override
    public Blueprint convert(Blueprint blueprint) {
        return blueprint.duplicate();
    }
}
