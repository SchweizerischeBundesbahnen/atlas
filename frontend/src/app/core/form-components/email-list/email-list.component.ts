import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-email-list',
  templateUrl: './email-list.component.html',
  styleUrls: ['./email-list.component.scss']
})
export class EmailListComponent {
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

