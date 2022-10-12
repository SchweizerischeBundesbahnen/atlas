import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { Record } from '../record';
import { UserAdministrationService } from '../../../../api';
import moment from 'moment/moment';
import { DATE_TIME_FORMAT } from '../../../date/date.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-user-detail-info [record]',
  templateUrl: './user-detail-info.component.html',
  styleUrls: ['./user-detail-info.component.scss'],
})
export class UserDetailInfoComponent<TYPE extends Record> implements OnInit, OnDestroy, OnChanges {
  @Input() record!: TYPE;
  editor!: string | undefined;
  editionDate!: string | undefined;
  creator!: string | undefined;
  creationDate!: string | undefined;
  private getUserSubscription!: Subscription;

  constructor(private readonly userAdministrationService: UserAdministrationService) {}

  ngOnInit(): void {
    this.getUserDetails();
  }

  getUserDetails() {
    if (this.record.editor) {
      this.populateEditor(this.record.editor);
    }
    if (this.record.creator) {
      this.populateCreator(this.record.creator);
    }
  }

  ngOnDestroy(): void {
    this.getUserSubscription.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes) {
      this.ngOnInit();
    }
  }

  private populateEditor(editor: any) {
    this.getUserSubscription = this.userAdministrationService.getUser(editor).subscribe((user) => {
      if (!user.displayName) {
        this.editor = this.record.editor;
      } else {
        this.editor = user.displayName;
      }
      this.editionDate = this.formatDateTime(this.record.editionDate);
    });
  }

  private populateCreator(user: any) {
    this.getUserSubscription = this.userAdministrationService.getUser(user).subscribe((user) => {
      if (!user.displayName) {
        this.creator = this.record.creator;
      } else {
        this.creator = user.displayName;
      }
      this.creationDate = this.formatDateTime(this.record.creationDate);
    });
  }

  private formatDateTime(dateTime: string | undefined) {
    return moment(dateTime).format(DATE_TIME_FORMAT);
  }
}
