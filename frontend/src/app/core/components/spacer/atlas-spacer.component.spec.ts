import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AtlasSpacerComponent } from './atlas-spacer.component';
import { AppTestingModule } from '../../../app.testing.module';

let component: AtlasSpacerComponent;
let fixture: ComponentFixture<AtlasSpacerComponent>;

describe('AtlasSpacerComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AtlasSpacerComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AtlasSpacerComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
