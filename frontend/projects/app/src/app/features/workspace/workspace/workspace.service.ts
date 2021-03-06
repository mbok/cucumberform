import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import {
  StepsCollection,
  Workspace,
  WorkspacesResult,
} from "./workspace.model";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class WorkspaceService {
  constructor(private http: HttpClient) {}

  public fetch(): Observable<WorkspacesResult> {
    return this.http.get<WorkspacesResult>("/api/v1/workspaces");
  }

  public get(id: string): Observable<Workspace> {
    return this.http.get<Workspace>(`/api/v1/workspaces/${id}`);
  }

  public getStepsCollection(id: string): Observable<StepsCollection> {
    return this.http.get<StepsCollection>(
      `/api/v1/workspaces/${id}/stepsCollection`
    );
  }

  update(id: string, workspace: Workspace) {
    return this.http.put<Workspace>(`/api/v1/workspaces/${id}`, workspace);
  }

  patch(id: string, patch: Workspace) {
    return this.http.patch<Workspace>(`/api/v1/workspaces/${id}`, patch);
  }

  getTemplate() {
    return this.http.get<Workspace>(`/api/v1/workspaces/template`);
  }

  create(workspace: Workspace) {
    return this.http.post<Workspace>(`/api/v1/workspaces`, workspace);
  }

  delete(id: string) {
    return this.http.delete<void>(`/api/v1/workspaces/${id}`);
  }
}
