import eslint from '@eslint/js';
import { defineConfig } from 'eslint/config';
import tseslint from 'typescript-eslint';
import angular from 'angular-eslint';
import eslintConfigPrettier from 'eslint-config-prettier/flat';

export default defineConfig([
  {
    files: ['**/*.ts'],
    extends: [
      eslint.configs.recommended,
      tseslint.configs.recommended,
      angular.configs.tsRecommended,
      eslintConfigPrettier,
    ],
    ignores: ['**node_modules/**/*', 'src/app/api/**/*', 'cypress.config.ts', 'coverage/**/*'],
    processor: angular.processInlineTemplates,

    rules: {
      '@angular-eslint/directive-selector': [
        'error',
        {
          type: 'attribute',
          style: 'camelCase',
          prefix: 'atlas',
        },
      ],

      '@angular-eslint/component-selector': [
        'error',
        {
          type: 'element',
          style: 'kebab-case',
          prefix: 'atlas',
        },
      ],

      '@typescript-eslint/no-non-null-assertion': 'off',
      '@typescript-eslint/explicit-module-boundary-types': 'off',
      '@angular-eslint/prefer-on-push-component-change-detection': 'off',
    },
  },
  {
    files: ['cypress/**/*.ts'],
    extends: [eslint.configs.recommended, tseslint.configs.recommended, eslintConfigPrettier],

    rules: {
      '@typescript-eslint/no-explicit-any': 'off',
    },
  },
]);
