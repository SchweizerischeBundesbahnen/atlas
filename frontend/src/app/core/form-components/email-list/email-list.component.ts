import {Component, ContentChild, EventEmitter, Input, Output, TemplateRef} from '@angular/core';
import {FieldExample} from "../text-field/field-example";
import {AtlasFieldCustomError} from "../atlas-field-error/atlas-field-custom-error";

@Component({
  selector: 'atlas-email-list',
  templateUrl: './email-list.component.html',
  styleUrls: ['./email-list.component.scss']
})
export class EmailListComponent {
  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() required!: boolean;
  @Input() fieldExamples!: Array<FieldExample>;
  @Input() customError!: AtlasFieldCustomError;
  @ContentChild('customChildInputPostfixTemplate')
    // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  customChildInputPostfixTemplate!: TemplateRef<any>;
  @ContentChild('customChildInputPrefixTemplate')
    // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  customChildInputPrefixTemplate!: TemplateRef<any>;
  @Input() existingEmails: string[] = [];
  email: string = '';
  emailList: string[] = [];
  maxEmails: number = 10;

  @Output() emailsChange = new EventEmitter<string[]>();

  ngOnInit() {
    if (this.existingEmails) {
      this.emailList = this.existingEmails;
    }
  }

  addEmail(email: string) {
    if (email && this.emailList.length < this.maxEmails && !this.emailList.includes(email)) {
      this.emailList.push(email);
      this.emailsChange.emit(this.emailList);
    }
  }

  removeEmail(index: number) {
    this.emailList.splice(index, 1);
    this.emailsChange.emit(this.emailList);
  }
}

