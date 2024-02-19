import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ParkingLotFormComponent} from './parking-lot-form.component';
import {TextFieldComponent} from '../../../../../../../core/form-components/text-field/text-field.component';
import {AtlasLabelFieldComponent} from '../../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import {MockAtlasFieldErrorComponent} from '../../../../../../../app.testing.mocks';
import {AtlasSpacerComponent} from '../../../../../../../core/components/spacer/atlas-spacer.component';
import {InfoIconComponent} from '../../../../../../../core/form-components/info-icon/info-icon.component';
import {SelectComponent} from '../../../../../../../core/form-components/select/select.component';
import {CommentComponent} from '../../../../../../../core/form-components/comment/comment.component';
import {AppTestingModule} from '../../../../../../../app.testing.module';
import {TranslatePipe} from '@ngx-translate/core';
import {ParkingLotFormGroupBuilder} from "../parking-lot-form-group";

describe('ParkingLotFormComponent', () => {
  let component: ParkingLotFormComponent;
  let fixture: ComponentFixture<ParkingLotFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        ParkingLotFormComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasSpacerComponent,
        InfoIconComponent,
        SelectComponent,
        CommentComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    });
    fixture = TestBed.createComponent(ParkingLotFormComponent);
    component = fixture.componentInstance;
    component.form = ParkingLotFormGroupBuilder.buildFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
