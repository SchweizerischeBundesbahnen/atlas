import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwitchVersionComponent } from './switch-version.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { Record } from '../detail-wrapper/record';
import moment from 'moment';
import { Pages } from '../../../pages/pages';
import { CoverageComponent } from '../coverage/coverage.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('SwitchVersionComponent', () => {
  let component: SwitchVersionComponent;
  let fixture: ComponentFixture<SwitchVersionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SwitchVersionComponent, CoverageComponent],
      imports: [
        HttpClientTestingModule,
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
    const secondRecord: Record = {
      id: 2,
      validFrom: moment('1.1.2001', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2001', 'DD.MM.YYYY').toDate(),
    };
    const thirdRecord: Record = {
      id: 3,
      validFrom: moment('1.6.2002', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2002', 'DD.MM.YYYY').toDate(),
    };
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    component.currentRecord = firstRecord;
    component.records = records;
    component.pageType = Pages.TTFN;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should switch to second version', () => {
    //given
    expect(component.isCurrentRecord(component.records[0])).toBeTrue();
    expect(component.getIndexOfRecord(component.records[0])).toBe(0);
    spyOn(component.switchVersion, 'emit');
    //when
    component.setCurrentRecord(component.records[1]);
    //then
    expect(component.currentIndex).toBe(1);
    expect(component.switchVersion.emit).toHaveBeenCalledWith(1);
  });

  it('should switch to first version', () => {
    //given
    component.setCurrentRecord(component.records[1]);
    spyOn(component.switchVersion, 'emit');
    //when
    component.setCurrentRecord(component.records[0]);
    //then
    expect(component.currentIndex).toBe(0);
    expect(component.switchVersion.emit).toHaveBeenCalledWith(0);
  });

  it('should get initial data range', () => {
    //when
    const result = component.getStartDate();
    //then
    expect(result).toBe('01.01.2000');
  });

  it('should get end data range', () => {
    //when
    const result = component.getEndDate();
    //then
    expect(result).toBe('31.12.2002');
  });

  it('should get end data range', () => {
    //when
    const result = component.getEndDate();
    //then
    expect(result).toBe('31.12.2002');
  });

  it('should evaluate gaps', () => {
    expect(component.hasGapToNextRecord(component.records[0])).toBeFalse();
    expect(component.hasGapToNextRecord(component.records[1])).toBeTrue();
    expect(component.hasGapToNextRecord(component.records[2])).toBeFalse();
  });
});
