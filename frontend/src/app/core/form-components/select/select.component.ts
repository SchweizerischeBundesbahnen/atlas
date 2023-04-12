import { Component, ContentChild, Input, OnInit, TemplateRef } from '@angular/core';
import { FormGroup } from '@angular/forms';

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
  @Input() dataCy!: string;

  @Input() controlName!: string;
  @Input() formGroup!: FormGroup;

  @Input() options: TYPE[] = [];
  @Input() optionValue: any;

  @ContentChild('matOptionPrefix') matOptionPrefix!: TemplateRef<any>;

  ngOnInit(): void {
    console.log(this.controlName);
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
}
