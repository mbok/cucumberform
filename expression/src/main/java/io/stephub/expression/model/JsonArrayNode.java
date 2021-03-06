package io.stephub.expression.model;

import io.stephub.expression.EvaluationContext;
import io.stephub.json.Json;
import io.stephub.json.JsonArray;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class JsonArrayNode extends JsonValueNode<Json> {
    private final List<JsonValueNode<? extends Json>> valueNodes;

    @Override
    public Json evaluate(final EvaluationContext ec) {
        return new JsonArray(this.valueNodes.stream().
                map(in -> in.evaluate(ec)).collect(Collectors.toList()));
    }
}
