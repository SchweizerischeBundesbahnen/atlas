import { inject, Service } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { email, pattern, required, SchemaPath, SchemaPathTree, validate, validateTree } from '@angular/forms/signals';
import { Moment } from 'moment';
import { DATE_PATTERN } from '../date/date.service';

@Service()
export class FormValidators {
  private readonly translateService = inject(TranslateService);

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  public required(path: SchemaPath<any>) {
    return required(path, { message: () => this.translateService.instant('VALIDATION.REQUIRED') });
  }

  public maxLength(path: SchemaPath<string>, maxLength: number) {
    return validate(path, ({ value }) => {
      if (value()?.length > maxLength) {
        return {
          kind: 'maxlength',
          message: this.translateService.instant('VALIDATION.MAXLENGTH', { length: maxLength }),
        };
      }

      return null;
    });
  }

  public email(path: SchemaPath<string>) {
    return email(path, {
      message: () => this.translateService.instant('VALIDATION.PATTERN', { allowedChars: 'E-Mail Format' }),
    });
  }

  public numeric(path: SchemaPath<string>) {
    return pattern(path, /^\d*$/u, {
      message: () => this.translateService.instant('VALIDATION.PATTERN', { allowedChars: '0-9' }),
    });
  }

  public iso88591(path: SchemaPath<string>) {
    return pattern(path, /^[\u0020-\u00ff]*$/u, {
      message: () => this.translateService.instant('VALIDATION.PATTERN', { allowedChars: 'ISO-8859-1' }),
    });
  }

  public blankOrEmptySpaceSurrounding(path: SchemaPath<string>) {
    return validate(path, ({ value }) => {
      if (value()) {
        if (value().trim().length === 0) {
          return {
            kind: 'whitespaces',
            message: this.translateService.instant('VALIDATION.BLANK'),
          };
        }

        if (value().startsWith(' ') || value().endsWith(' ')) {
          return {
            kind: 'whitespaces',
            message: this.translateService.instant('VALIDATION.WHITESPACES'),
          };
        }
      }

      return null;
    });
  }

  public validToAfterOrEqualValidFrom(
    schemaPath: SchemaPathTree<{ validFrom: Moment | null; validTo: Moment | null }>
  ) {
    return validateTree(schemaPath, (ctx) => {
      const validFromValue = ctx.valueOf(schemaPath.validFrom);
      const validToValue = ctx.valueOf(schemaPath.validTo);

      if (validFromValue !== null && validToValue !== null && validFromValue.isAfter(validToValue)) {
        const dateOrderMessage = this.translateService.instant('VALIDATION.DATE_ORDER_ERROR', {
          validFrom: validFromValue.format(DATE_PATTERN),
          validTo: validToValue.format(DATE_PATTERN),
        });
        return [
          {
            kind: 'dateRange',
            message: dateOrderMessage,
            fieldTree: ctx.fieldTree.validFrom,
          },
          {
            kind: 'dateRange',
            message: dateOrderMessage,
            fieldTree: ctx.fieldTree.validTo,
          },
        ];
      }

      return null;
    });
  }
}
