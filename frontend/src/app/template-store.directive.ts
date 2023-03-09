import { Directive, Input, TemplateRef } from '@angular/core';
import { TemplateStore } from './template-store';

@Directive({
  selector: '[formTemplateKey]',
})
export class TemplateStoreDirective {
  @Input()
  set formTemplateKey(key: string) {
    this.templateStore.addTemplate(key, this.template);
  }

  constructor(
    private readonly template: TemplateRef<any>,
    private readonly templateStore: TemplateStore
  ) {}
}
