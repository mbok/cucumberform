package io.stephub.runtime.controller;

import io.stephub.json.schema.JsonSchema;
import io.stephub.provider.api.model.spec.StepSpec;
import io.stephub.runtime.model.Context;
import io.stephub.runtime.service.ProvidersFacade;
import io.stephub.runtime.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class StepsCollectionController {
    @Autowired
    private ProvidersFacade providersFacade;

    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/workspaces/{wid}/stepsCollection")
    @ResponseBody
    public Map<String, List<StepSpec<JsonSchema>>> getStepSpecs(@ModelAttribute final Context ctx, @PathVariable("wid") final String wid) {
        return this.providersFacade.getStepsCollection(this.workspaceService.getWorkspace(ctx, wid));
    }
}
