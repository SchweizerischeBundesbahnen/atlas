import { NgModule } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { DateModule } from './core/module/date.module';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { translateServiceProvider } from './app.testing.mocks';

const dialogMock = {
  close: () => {
    // Mock implementation
  },
};

@NgModule({
  imports: [DateModule.forRoot(), ReactiveFormsModule, RouterModule.forRoot([])],
  exports: [DateModule, ReactiveFormsModule, RouterModule],
  providers: [{ provide: MatDialogRef, useValue: dialogMock }, { provide: TranslatePipe }, translateServiceProvider],
})
export class AppTestingModule {}
