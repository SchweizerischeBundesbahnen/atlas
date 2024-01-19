import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { UserAdministrationService } from '../../../../api';
import moment from 'moment';
import { DATE_PATTERN, DATE_TIME_FORMAT_WITHOUT_SECONDS } from '../../../date/date.service';
import { catchError, forkJoin, Observable, of } from 'rxjs';
import { CreationEditionRecord } from './creation-edition-record';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-user-detail-info [record]',
  templateUrl: './user-detail-info.component.html',
  styleUrls: ['./user-detail-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserDetailInfoComponent {
  @Input() short = false;

  private _record$: Observable<CreationEditionRecord | undefined> = of(undefined);

  constructor(private readonly userAdministrationService: UserAdministrationService) {}

  @Input()
  set record(record: CreationEditionRecord) {
    this._record$ = this.getProcessedCreationEdition(record);
  }

  get processedRecord(): Observable<CreationEditionRecord | undefined> {
    return this._record$;
  }

  private getProcessedCreationEdition(
    record: CreationEditionRecord,
  ): Observable<CreationEditionRecord | undefined> {
    const displayNames$: Observable<string | undefined>[] = [record.editor, record.creator].map(
      (value) => {
        if (!value) {
          return of(undefined);
        }
        return this.userAdministrationService
          .getUserDisplayName(value)
          .pipe(
            map(
              (userDisplayName) =>
                this.formatUserDisplayInformation(userDisplayName.displayName) ?? value,
            ),
          );
      },
    );

    return forkJoin(displayNames$).pipe(
      map(([editor, creator]) => ({
        editionDate: this.formatDateTime(record.editionDate),
        editionDateWithoutTime: this.formatDate(record.editionDate),
        creationDate: this.formatDateTime(record.creationDate),
        editor,
        creator,
      })),
      catchError(() => of(undefined)),
    );
  }

  private formatUserDisplayInformation(displayName?: string) {
    const indexOfParenthesis = displayName?.indexOf('(');
    if (displayName && indexOfParenthesis != -1) {
      return displayName?.substring(0, indexOfParenthesis);
    }
    return displayName;
  }

  private formatDateTime(dateTime: string | undefined) {
    return moment(dateTime).format(DATE_TIME_FORMAT_WITHOUT_SECONDS);
  }

  private formatDate(dateTime: string | undefined) {
    return moment(dateTime).format(DATE_PATTERN);
  }
}
