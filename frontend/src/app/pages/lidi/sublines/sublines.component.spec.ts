import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ContainerSubline, Status, SublinesService, SublineType } from '../../../api';
import { SublinesComponent } from './sublines.component';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';

const versionContainer: ContainerSubline = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      status: Status.Validated,
      businessOrganisation: 'SBB',
      swissSublineNumber: 'L1:2',
      sublineType: SublineType.Technical,
    },
  ],
  totalCount: 1,
};

describe('SublinesComponent', () => {
  let component: SublinesComponent;
  let fixture: ComponentFixture<SublinesComponent>;

  // With Spy
  const sublinesService = jasmine.createSpyObj('linesService', ['getSublines']);
  sublinesService.getSublines.and.returnValue(of(versionContainer));

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SublinesComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: SublinesService, useValue: sublinesService }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(SublinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
