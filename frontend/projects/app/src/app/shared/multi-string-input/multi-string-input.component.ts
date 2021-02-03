import {
  ChangeDetectionStrategy,
  Component,
  ContentChild,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from "@angular/core";
import { CdkDragDrop, moveItemInArray } from "@angular/cdk/drag-drop";
import { FormControl, ValidatorFn } from "@angular/forms";
import { MultiStringViewDirective } from "./multi-string-view.directive";
import { BehaviorSubject, Observable } from "rxjs";
import { map, startWith } from "rxjs/operators";
import { MultiStringAutoSuggestOptionDirective } from "./multi-string-auto-suggest-option.directive";

@Component({
  selector: "sh-multi-string-input",
  templateUrl: "./multi-string-input.component.html",
  styleUrls: ["./multi-string-input.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MultiStringInputComponent implements OnInit {
  @ContentChild(MultiStringViewDirective) itemViewTpl: MultiStringViewDirective;
  @ContentChild(MultiStringAutoSuggestOptionDirective)
  autoSuggestOptionTpl: MultiStringAutoSuggestOptionDirective;

  @Input() editMode: boolean = true;

  @Input() validator: ValidatorFn | ValidatorFn[] | null;

  @Input() sequence: string[];

  @Input() textarea: boolean = false;

  @Input() codeInput: boolean = false;

  @Input() order: boolean = true;

  @Input() autoCompleteSource: (value: string) => SuggestOption[];

  @Output() sequenceChange = new EventEmitter<string[]>();

  newItemCtrl: FormControl;
  columns: string[];
  autoCompleteItems$: Observable<SuggestOption[]>;
  newAutoCompleteItems$: Observable<SuggestOption[]>;
  autoCompleteSelected$ = new BehaviorSubject(false);

  controlFactory: (value: any) => FormControl = (givenValue) => {
    let control = new FormControl(givenValue, this.validator);
    this.autoCompleteItems$ = control.valueChanges.pipe(
      startWith(givenValue),
      map((value) => this.filterAutoComplete(value))
    );
    return control;
  };

  ngOnInit(): void {
    this.newItemCtrl = new FormControl("", this.validator);
    this.newAutoCompleteItems$ = this.newItemCtrl.valueChanges.pipe(
      startWith(""),
      map((value) => this.filterAutoComplete(value))
    );
    this.columns = this.order ? ["drag", "item", "action"] : ["item", "action"];
  }

  private updateSequence() {
    this.sequence = [...this.sequence];
    this.sequenceChange.emit(this.sequence);
  }

  onItemDrop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.sequence, event.previousIndex, event.currentIndex);
    this.updateSequence();
  }

  addItem() {
    if (!this.newItemCtrl.valid) {
      return;
    }
    if (!this.sequence) {
      this.sequence = [];
    }
    this.sequence.push(this.newItemCtrl.value);
    this.updateSequence();
    this.newItemCtrl.setValue("");
    this.newItemCtrl.setErrors(null);
  }

  deleteItem(index: number) {
    this.sequence.splice(index, 1);
    this.updateSequence();
  }

  addItemOnEnter(event: KeyboardEvent) {
    if (
      !event.shiftKey &&
      this.newItemCtrl.value.indexOf("\n") < 0 &&
      this.newItemCtrl.value.trim().length > 0 &&
      !this.autoCompleteSelected$.value
    ) {
      this.addItem();
      event.preventDefault();
      (event.target as HTMLElement).blur();
    }
  }

  saveItem(index: number, newValue: string) {
    this.sequence[index] = newValue;
    this.updateSequence();
  }

  fieldErrors(fc: FormControl): string[] {
    const errors = [];
    if (fc.errors) {
      Object.keys(fc.errors).forEach((k) => errors.push(fc.errors[k]));
    }
    return errors;
  }

  clearNewItem() {
    this.newItemCtrl.setValue("");
    this.newItemCtrl.setErrors(null);
  }

  filterAutoComplete(value) {
    if (this.autoCompleteSource) {
      return this.autoCompleteSource(value);
    }
    return [];
  }

  markAutoCompleteSelected() {
    this.autoCompleteSelected$.next(true);
    setTimeout(() => this.autoCompleteSelected$.next(false), 500);
  }
}

export interface SuggestOption {
  value: string;
  view: any;
}
