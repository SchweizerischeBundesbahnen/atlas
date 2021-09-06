import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableComponent } from './table.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MaterialModule } from '../../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';

describe('TableComponent', () => {
  /*eslint-disable */
  let component: TableComponent<any>;
  let fixture: ComponentFixture<TableComponent<any>>;
  /*eslint-enable */

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TableComponent],
      imports: [
        MaterialModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableComponent);
    component = fixture.componentInstance;
    component.tableColumns = [
      {
        headerTitle: 'TTFN.VALID_FROM',
        value: 'validFrom',
        formatAsDate: true,
      },
      { headerTitle: 'TTFN.VALID_TO', value: 'validTo', formatAsDate: true },
    ];
    component.tableData = [{ validFrom: new Date('2021-12-31'), validTo: new Date('2099-12-31') }];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render date nicely', () => {
    const firstTableCell = fixture.debugElement.query(By.css('td'));
    expect(firstTableCell.nativeElement.innerText).toBe('31.12.2021');
  });

  it('should not render add if user may not edit', () => {
    component.canEdit = false;
    fixture.detectChanges();

    const addButton = fixture.debugElement.query(By.css('.bi-plus'));
    expect(addButton).toBeFalsy();
  });

  it('should render add if user can edit', () => {
    component.canEdit = true;
    fixture.detectChanges();

    const addButton = fixture.debugElement.query(By.css('.bi-plus'));
    expect(addButton).toBeTruthy();
  });

  it('should output new event', () => {
    spyOn(component.newElementEvent, 'emit');

    const addButton = fixture.debugElement.query(By.css('.bi-plus'));
    addButton.nativeElement.click();

    expect(component.newElementEvent.emit).toHaveBeenCalled();
  });

  it('should output edit event', () => {
    spyOn(component.editElementEvent, 'emit');

    const firstTableCell = fixture.debugElement.query(By.css('td'));
    firstTableCell.nativeElement.click();

    expect(component.editElementEvent.emit).toHaveBeenCalled();
  });
});
