import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {AtlasFieldLengthValidator} from "../../validation/field-lengths/atlas-field-length-validator";
import {AtlasCharsetsValidator} from "../../validation/charsets/atlas-charsets-validator";

@Component({
  selector: 'atlas-email-list',
  templateUrl: './email-list.component.html',
  styleUrls: ['./email-list.component.scss']
})
export class EmailListComponent {
  @Input() disabled: boolean = false;
  @Input() formGroup!: FormGroup;
  @Input() controlName!: string;
  emailList: string[] = [];
  maxEmails = 10;

  formGroupEmailList!: FormGroup;

  @Output() emailsChange = new EventEmitter<string[]>();

  ngOnInit() {
    this.formGroupEmailList = new FormGroup({
      myStringParameter: new FormControl('', [
        AtlasFieldLengthValidator.length_100,
        AtlasCharsetsValidator.email
      ])
    });
    this.emailList = this.formGroup.get('statementSender')!.get('emails')!.value;
  }

  addEmail() {
    const email = this.formGroupEmailList.get('myStringParameter')!.value;
    if (email && this.emailList.length < this.maxEmails && !this.emailList.includes(email)) {
      this.emailList.push(email);
      this.emailsChange.emit(this.emailList);
    }
  }

  emptyInputField() {
    this.formGroupEmailList.get('myStringParameter')!.setValue('');
  }

  removeEmail(index: number) {
    this.emailList.splice(index, 1);
    this.emailsChange.emit(this.emailList);
  }
}

