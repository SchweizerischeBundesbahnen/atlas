import { ChangeDetectionStrategy, Component, inject, input, Input } from '@angular/core';
import moment from 'moment';
import { DATE_PATTERN, DATE_TIME_FORMAT_WITHOUT_SECONDS } from '../../date/date.service';
import { catchError, forkJoin, Observable, of } from 'rxjs';
import { CreationEditionRecord } from './creation-edition-record';
import { map } from 'rxjs/operators';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationService } from '../../../api/service/user-administration/user-administration.service';
import { ApplicationType } from '../../../api';
import { UserOpenInMailComponent } from '../user-open-in-mail/user-open-in-mail.component';

@Component({
  selector: 'atlas-user-detail-info',
  templateUrl: './user-detail-info.component.html',
  styleUrls: ['./user-detail-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AsyncPipe, TranslatePipe, UserOpenInMailComponent],
  providers: [TranslatePipe],
})
export class UserDetailInfoComponent {
  short = input(false);
  showEditor = input(true);
  applicationType = input<ApplicationType>();

  private _record$: Observable<CreationEditionRecord | undefined> = of(undefined);

  readonly userAdministrationService = inject(UserAdministrationService);

  @Input()
  set record(record: CreationEditionRecord) {
    this._record$ = this.getProcessedCreationEdition(record);
  }

  get processedRecord(): Observable<CreationEditionRecord | undefined> {
    return this._record$;
  }

  private getProcessedCreationEdition(record: CreationEditionRecord): Observable<CreationEditionRecord | undefined> {
    const displayNames$: Observable<string | undefined>[] = [record.editor, record.creator].map((value) => {
      if (!value) {
        return of(undefined);
      }
      return this.userAdministrationService
        .getUserDisplayName(value)
        .pipe(map((userDisplayName) => this.formatUserDisplayInformation(userDisplayName.displayName) ?? value));
    });

    return forkJoin(displayNames$).pipe(
      map(([editor, creator]) => ({
        editionDate: this.formatDateTime(record.editionDate),
        editionDateWithoutTime: this.formatDate(record.editionDate),
        creationDate: this.formatDateTime(record.creationDate),
        creatorDisplayName: creator,
        editorDisplayName: editor,
        editor: record.editor,
        creator: record.creator,
      })),
      catchError(() => of(undefined))
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
