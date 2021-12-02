import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwitchVersionComponent } from './switch-version.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { Record } from '../detail-wrapper/record';
import moment from 'moment';

describe('SwitchVersionComponent', () => {
  let component: SwitchVersionComponent;
  let fixture: ComponentFixture<SwitchVersionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SwitchVersionComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SwitchVersionComponent);
    component = fixture.componentInstance;
    const firstRecord: Record = {
      id: 1,
      validFrom: moment('1.1.2000', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2000', 'DD.MM.YYYY').toDate(),
    };
    const records: Array<Record> = [firstRecord];
    component.currentRecord = firstRecord;
    component.records = records;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
