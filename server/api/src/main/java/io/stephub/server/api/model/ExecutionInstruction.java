package io.stephub.server.api.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.stephub.server.api.model.Execution.ExecutionItem;
import io.stephub.server.api.model.Execution.FeatureExecutionItem;
import io.stephub.server.api.model.Execution.ScenarioExecutionItem;
import io.stephub.server.api.model.Execution.ScenarioExecutionItem.ScenarioExecutionItemBuilder;
import io.stephub.server.api.model.Execution.StepExecutionItem;
import io.stephub.server.api.model.gherkin.Feature;
import io.stephub.server.api.model.gherkin.Scenario;
import io.stephub.server.api.validation.ValidRegex;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExecutionInstruction.StepsExecutionInstruction.class, name = "step"),
        @JsonSubTypes.Type(value = ExecutionInstruction.ScenariosExecutionInstruction.class, name = "scenarios")
})
public abstract class ExecutionInstruction {

    public abstract List<ExecutionItem> buildItems(Workspace workspace);

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StepsExecutionInstruction extends ExecutionInstruction {
        @Size(min = 1)
        @Singular
        private List<String> steps;

        @Override
        public List<ExecutionItem> buildItems(final Workspace workspace) {
            return this.steps.stream().map(s -> StepExecutionItem.builder().step(s).build()).collect(Collectors.toList());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class ScenariosExecutionInstruction extends ExecutionInstruction {
        @NotNull
        @Builder.Default
        @Valid
        private ScenarioFilter filter = new AllScenarioFilter();

        @Override
        public List<ExecutionItem> buildItems(final Workspace workspace) {
            return this.getFiltered(workspace);
        }

        private List<ExecutionItem> getFiltered(final Workspace workspace) {
            final List<ExecutionItem> featureItems = new ArrayList<>();
            workspace.getFeatures().forEach(
                    feature ->
                    {
                        final List<Scenario> scenarios = feature.getScenarios().stream().filter(s -> this.filter.accept(feature, s)).collect(Collectors.toList());
                        if (!scenarios.isEmpty()) {
                            final FeatureExecutionItem featureItem = FeatureExecutionItem.builder().name(feature.getName()).
                                    scenarios(this.buildScenarioItems(feature, scenarios)).build();
                            featureItems.add(featureItem);
                        }
                    }
            );
            return featureItems;
        }

        private List<ScenarioExecutionItem> buildScenarioItems(final Feature feature, final List<Scenario> scenarios) {
            return scenarios.stream().map(s -> this.buildScenarioItem(feature, s)).collect(Collectors.toList());
        }

        private ScenarioExecutionItem buildScenarioItem(final Feature feature, final Scenario scenario) {
            final ScenarioExecutionItemBuilder<?, ?> builder = ScenarioExecutionItem.builder().name(scenario.getName());
            if (feature.getBackground() != null) {
                feature.getBackground().getSteps().forEach(step -> builder.step(
                        StepExecutionItem.builder().step(step).build()
                ));
            }
            scenario.getSteps().forEach(step -> builder.step(
                    StepExecutionItem.builder().step(step).build()
            ));
            return builder.build();
        }
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            defaultImpl = AllScenarioFilter.class,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = ExecutionInstruction.AllScenarioFilter.class, name = "all"),
            @JsonSubTypes.Type(value = ExecutionInstruction.NamedScenarioFilter.class, name = "by-scenario-name"),
            @JsonSubTypes.Type(value = ExecutionInstruction.NamedFeatureFilter.class, name = "by-feature-name"),
            @JsonSubTypes.Type(value = ExecutionInstruction.TagFilter.class, name = "by-tag")
    })
    public interface ScenarioFilter {
        boolean accept(Feature feature, Scenario scenario);
    }

    public static class AllScenarioFilter implements ScenarioFilter {
        @Override
        public boolean accept(final Feature feature, final Scenario scenario) {
            return true;
        }
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public abstract static class PatternFilter implements ScenarioFilter {
        @NotEmpty
        @Valid
        private List<@NotNull @ValidRegex String> patterns;

        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        private List<Pattern> compiledPatterns;

        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        private int lastCompileHashCode;

        protected boolean accept(final String subject) {
            return this.getCompiledPatterns().stream().anyMatch(pattern -> pattern.matcher(subject).find());
        }

        private List<Pattern> getCompiledPatterns() {
            if (this.compiledPatterns == null || this.lastCompileHashCode != this.patterns.hashCode()) {
                this.compiledPatterns = this.patterns.stream().map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE)).collect(Collectors.toList());
                this.lastCompileHashCode = this.patterns.hashCode();
            }
            return this.compiledPatterns;
        }
    }

    @SuperBuilder
    @NoArgsConstructor
    public static class NamedScenarioFilter extends PatternFilter {

        @Override
        public boolean accept(final Feature feature, final Scenario scenario) {
            return this.accept(scenario.getName());
        }
    }

    @SuperBuilder
    @NoArgsConstructor
    public static class NamedFeatureFilter extends PatternFilter {

        @Override
        public boolean accept(final Feature feature, final Scenario scenario) {
            return this.accept(feature.getName());
        }
    }

    @SuperBuilder
    @NoArgsConstructor
    public static class TagFilter extends PatternFilter {

        @Override
        public boolean accept(final Feature feature, final Scenario scenario) {
            return feature.getTags().stream().anyMatch(this::accept) ||
                    scenario.getTags().stream().anyMatch(this::accept);
        }
    }
}
