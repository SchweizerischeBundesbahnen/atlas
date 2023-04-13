import {
  Component,
  ContentChild,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'atlas-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.scss'],
})
export class SelectComponent<TYPE> implements OnInit {
  @Input() label: string | undefined;
  @Input() optionTranslateLabelPrefix: string | undefined;
  @Input() additionalLabelspace = true;
  @Input() required = false;

  private _disabled = false;
  @Input()
  set disabled(value: boolean) {
    this._disabled = value;
    if (this._isDummyForm) {
      if (this.disabled) {
        this.formGroup.disable();
      } else {
        this.formGroup.enable();
      }
    }
  }

  get disabled(): boolean {
    return this._disabled;
  }

  @Input() multiple = false;
  @Input() dataCy!: string;

  @Input() controlName: string | null = null;
  @Input() formGroup!: FormGroup;

  @Input() options: TYPE[] = [];
  @Input() optionValue: any;

  @ContentChild('matOptionPrefix') matOptionPrefix!: TemplateRef<any>;

  @Input() initialValue: any;

  @Output() selectChanged = new EventEmitter();

  private _isDummyForm = false;

  ngOnInit(): void {
    if (!this.formGroup) {
      this.initDummyForm();
    }
    if (this.initialValue) {
      this.formGroup.get(this.controlName!)?.setValue(this.initialValue);
    }
  }

  @Input()
  valueExtractor(option: TYPE): any {
    return option;
  }

  @Input()
  displayExtractor(option: TYPE): any {
    return option;
  }

  getAsObject(option: TYPE): object {
    return {
      option: option,
    };
  }

  private initDummyForm() {
    this.formGroup = new FormGroup<any>({
      dummy: new FormControl(),
    });
    this.controlName = 'dummy';

    this._isDummyForm = true;
    if (this.disabled) {
      this.formGroup.disable();
    }
  }
}
