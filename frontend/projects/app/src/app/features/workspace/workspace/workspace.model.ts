import { FieldError } from "../../../core/server-error/server-error.model";
import { StepSpec } from "../step/step.model";

export interface Workspace {
  id: string;
  name: string;
  features: Feature[];
  gherkinPreferences: GherkinPreferences;
  variables: VariableMap;
  errors?: FieldError[];
  providers: ProviderSpec[];
}

export interface ProviderSpec {
  name: string;
  version?: string;
  options?: object;
  sessionTimeout?: string;
  remoteConfig?: ProviderRemoteConfig;
}

export interface ProviderRemoteConfig {
  url: string;
}

export interface WorkspacesResult {
  total: number;
  items: Workspace[];
}

export interface VariableMap {
  [key: string]: Variable;
}

export interface Variable {
  defaultValue: any;
  description: string;
  schema: object;
}

export interface GherkinPreferences {
  assignmentKeywords: string[];
  stepKeywords: string[];
}

export interface Annotatable {
  tags?: string[];
  comments?: string[];
}

export interface StepSequence {
  steps?: string[];
}

export interface Scenario extends Annotatable, StepSequence {
  name: string;
}

export interface Feature extends Annotatable {
  name: string;
  background?: StepSequence;
  scenarios?: Scenario[];
}

export interface StepsCollection {
  [key: string]: StepSpec[];
}
