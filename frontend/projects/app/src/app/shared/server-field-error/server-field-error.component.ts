import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges,
} from "@angular/core";
import { FieldError } from "../../core/server-error/server-error.model";

@Component({
  selector: "sh-server-field-error",
  templateUrl: "./server-field-error.component.html",
  styleUrls: ["./server-field-error.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ServerFieldErrorComponent implements OnChanges {
  @Input() fieldErrors: FieldError[] | null;

  @Input() displayType: DisplayType = DisplayType.full;

  @Input() path: string | string[];

  @Input() exact: boolean = false;

  errors: string[];

  ngOnChanges(): void {
    this.errors = [];
    if (this.fieldErrors) {
      this.fieldErrors.forEach((error) => {
        if (Array.isArray(this.path)) {
          if (this.path.find((p) => this.matches(error, p))) {
            this.errors.push(error.message);
          }
        } else {
          if (this.matches(error, this.path)) {
            this.errors.push(error.message);
          }
        }
      });
    }
  }

  private matches(error: FieldError, path: string): boolean {
    return this.exact ? error.field == path : error.field.startsWith(path);
  }

  aggregatedTooltip() {
    return this.errors
      .map((value, index) => index + 1 + ") " + value)
      .join("\n");
  }
}

export enum DisplayType {
  indicator = "indicator",
  indicator_tooltip = "indicator-tooltip",
  full = "full",
}
