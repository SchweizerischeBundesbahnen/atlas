import { Injectable } from '@angular/core';
import { TimetableHearingStatement } from '../../../api';

@Injectable({
  providedIn: 'root',
})
export class StatementShareService {
  statement!: TimetableHearingStatement | undefined;
}
