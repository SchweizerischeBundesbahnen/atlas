import { Component, Input } from '@angular/core';
import { UserAdministrationService } from '../../../../api';
import moment from 'moment/moment';
import { DATE_TIME_FORMAT } from '../../../date/date.service';
import { catchError, forkJoin, Observable, of } from 'rxjs';
import { CreationEditionRecord } from './creation-edition-record';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-user-detail-info [record]',
  templateUrl: './user-detail-info.component.html',
  styleUrls: ['./user-detail-info.component.scss'],
})
export class UserDetailInfoComponent {
  @Input()
  set record(record: CreationEditionRecord) {
    this._record$ = this.getProcessedCreationEdition(record);
  }

  _record$: Observable<CreationEditionRecord | undefined> = of(undefined);

  constructor(private readonly userAdministrationService: UserAdministrationService) {}

  private getProcessedCreationEdition(
    record: CreationEditionRecord
  ): Observable<CreationEditionRecord | undefined> {
    const displayNames$: Observable<string | undefined>[] = [record.editor, record.creator].map(
      (value) => {
        if (!value) {
          return of(undefined);
        }
        return this.userAdministrationService
          .getUserDisplayName(value)
          .pipe(map((userDisplayName) => userDisplayName.displayName ?? value));
      }
    );

    return forkJoin(displayNames$).pipe(
      map(([editor, creator]) => ({
        editionDate: this.formatDateTime(record.editionDate),
        creationDate: this.formatDateTime(record.creationDate),
        editor,
        creator,
      })),
      catchError(() => of(undefined))
    );
  }

  private formatDateTime(dateTime: string | undefined) {
    return moment(dateTime).format(DATE_TIME_FORMAT);
  }
}
