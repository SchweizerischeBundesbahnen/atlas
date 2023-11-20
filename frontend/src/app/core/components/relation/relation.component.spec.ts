import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RelationComponent } from './relation.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MaterialModule } from '../../module/material.module';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('TransportCompanyRelationComponent', () => {
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let component: RelationComponent<any>;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let fixture: ComponentFixture<RelationComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RelationComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        MaterialModule,
        BrowserAnimationsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RelationComponent);
    component = fixture.componentInstance;
    component.tableColumns = [
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.SAID',
        valuePath: 'businessOrganisation.said',
        columnDef: 'said',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
        valuePath: 'businessOrganisation.organisationNumber',
        columnDef: 'organisationNumber',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
        valuePath: `businessOrganisation.abbreviationDe`,
        columnDef: 'abbreviation',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
        valuePath: `businessOrganisation.descriptionDe`,
        columnDef: 'description',
      },
      {
        headerTitle: 'COMMON.VALID_FROM',
        value: 'validFrom',
        columnDef: 'validFrom',
        formatAsDate: true,
      },
      {
        headerTitle: 'COMMON.VALID_TO',
        value: 'validTo',
        columnDef: 'validTo',
        formatAsDate: true,
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test columnValues function', () => {
    expect(component.columnValues()).toEqual([
      'said',
      'organisationNumber',
      'abbreviation',
      'description',
      'validFrom',
      'validTo',
    ]);
  });

  it('test isRowSelected function', () => {
    component.records = [
      { id: 1, value: 'test1' },
      { id: 2, value: 'test2' },
    ];
    component.selectedIndex = 1;
    expect(component.isRowSelected(component._records[1])).toBeTrue();
    expect(component.isRowSelected(component._records[0])).toBeFalse();
  });

  it('edit mode changed should emit event', () => {
    component.editable = true;
    fixture.detectChanges();
    let eventEmitted = false;
    component.editModeChanged.subscribe(() => (eventEmitted = true));
    const editBtn = fixture.debugElement.query(By.css('button'));
    editBtn.nativeElement.click();
    expect(eventEmitted).toBeTrue();
  });

  it('test select record', () => {
    component.records = [
      { id: 1, value: 'test1' },
      { id: 2, value: 'test2' },
    ];
    component.editable = true;
    component.selectedIndexChanged.subscribe((index) => expect(index).toBe(1));
    component.selectRecord(component._records[1]);

    component.editable = false;
    let selectedIndexChangedCalled = false;
    component.selectedIndexChanged.subscribe(() => (selectedIndexChangedCalled = true));
    component.selectRecord(component._records[0]);
    expect(selectedIndexChangedCalled).toBeFalse();
  });

  it('test delete', () => {
    component.editable = true;
    component.selectedIndex = 0;
    fixture.detectChanges();
    const deleteBtn = fixture.debugElement.queryAll(By.css('button'))[2];
    spyOn(component.deleteRelation, 'emit');
    deleteBtn.nativeElement.click();
    expect(component.deleteRelation.emit).toHaveBeenCalledOnceWith();
  });

  it('test update', () => {
    component.editable = true;
    component.selectedIndex = 0;
    fixture.detectChanges();
    const deleteBtn = fixture.debugElement.queryAll(By.css('button'))[1];
    spyOn(component.updateRelation, 'emit');
    deleteBtn.nativeElement.click();
    expect(component.updateRelation.emit).toHaveBeenCalledOnceWith();
  });
});
