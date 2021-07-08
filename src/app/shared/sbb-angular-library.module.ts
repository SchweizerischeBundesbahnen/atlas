import { NgModule } from '@angular/core';
import { SbbHeaderModule } from '@sbb-esta/angular-business/header';
import { SbbIconModule } from '@sbb-esta/angular-core/icon';
import { SbbSidebarModule } from '@sbb-esta/angular-business/sidebar';
import { SbbUsermenuModule } from '@sbb-esta/angular-business/usermenu';
import { SbbLinksModule } from '@sbb-esta/angular-business/links';

const MODULES = [
  SbbHeaderModule,
  SbbIconModule,
  SbbLinksModule,
  SbbSidebarModule,
  SbbUsermenuModule,
  // TODO: Add your required sbb-angular modules
];

@NgModule({
  imports: MODULES,
  exports: MODULES,
})
export class SbbAngularLibraryModule {}
