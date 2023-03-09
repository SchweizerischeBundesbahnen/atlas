import { TemplateRef } from '@angular/core';

export class TemplateStore {
  private templateStore: Map<string, TemplateRef<any>> = new Map<string, TemplateRef<any>>();

  addTemplate(key: string, template: TemplateRef<any>) {
    this.templateStore.set(key, template);
  }

  getTemplate(key: string): TemplateRef<any> | undefined {
    return this.templateStore.get(key);
  }
}
