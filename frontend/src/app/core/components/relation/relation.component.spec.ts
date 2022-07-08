import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RelationComponent } from './relation.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MaterialModule } from '../../module/material.module';

describe('TransportCompanyRelationComponent', () => {
  let component: RelationComponent<any>;
  let fixture: ComponentFixture<RelationComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RelationComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        MaterialModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RelationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test columnValues function', () => {
    component.tableColumns = [
      { headerTitle: 'test', value: 'test1' },
      { headerTitle: 'test', value: 'test2' },
    ];
    expect(component.columnValues()).toEqual(['test1', 'test2']);
  });

  it('test isRowSelected function', () => {
    component.records = [
      { id: 1, value: 'test1' },
      { id: 2, value: 'test2' },
    ];
    component['selectedIndex'] = 1;
    expect(component.isRowSelected(component.records[1])).toBeTrue();
    expect(component.isRowSelected(component.records[0])).toBeFalse();
  });

  it('create should emit event', () => {
    let eventEmitted = false;
    component.createRelation.subscribe(() => (eventEmitted = true));
    component.create();
    expect(eventEmitted).toBeTrue();
  });

  it('test select record', () => {
    component.records = [
      { id: 1, value: 'test1' },
      { id: 2, value: 'test2' },
    ];
    component.editable = true;
    component.selectRecord(component.records[1]);
    expect(component['selectedIndex']).toBe(1);

    component.editable = false;
    component.selectRecord(component.records[0]);
    expect(component['selectedIndex']).toBe(1);
  });

  it('test delete', (done) => {
    component.deleteRelation.subscribe((deleteEvent) => {
      expect(component.records.length > 0);
      expect(deleteEvent.record).toEqual({ id: 1, value: 'test1' });
      expect(deleteEvent.callbackFn).toBeDefined();
      done();
    });
    component.delete();

    component.records = [
      { id: 1, value: 'test1' },
      { id: 2, value: 'test2' },
    ];
    component.delete();
  });
});
