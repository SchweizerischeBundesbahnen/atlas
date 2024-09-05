const js = require("@eslint/js");

const {
    FlatCompat,
} = require("@eslint/eslintrc");

const compat = new FlatCompat({
    baseDirectory: __dirname,
    recommendedConfig: js.configs.recommended,
    allConfig: js.configs.all
});

module.exports = [{
    ignores: ["projects/**/*", "**node_modules/**/*", "src/app/api/**/*", "cypress.config.ts"],
}, ...compat.extends(
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:@angular-eslint/recommended",
    "plugin:@angular-eslint/template/process-inline-templates",
    "prettier",
).map(config => ({
    ...config,
    files: ["**/*.ts"],
})), {
    files: ["**/*.ts"],

    languageOptions: {
        ecmaVersion: 5,
        sourceType: "script",

        parserOptions: {
            project: ["tsconfig.json"],
            createDefaultProgram: true,
        },
    },

    rules: {
        "@angular-eslint/directive-selector": ["error", {
            type: "attribute",
            style: "camelCase",
        }],

        "@angular-eslint/component-selector": ["error", {
            type: "element",
            style: "kebab-case",
        }],

        "@typescript-eslint/no-non-null-assertion": "off",
        "@typescript-eslint/explicit-module-boundary-types": "off",
    },
}, {
  files: ["cypress/**/*.ts"],

  languageOptions: {
    ecmaVersion: 5,
    sourceType: "script",

    parserOptions: {
      project: ["cypress/tsconfig.json"],
      createDefaultProgram: true,
    },
  },

  rules: {
    "@typescript-eslint/no-explicit-any": "off",
  },
},
  ...compat.extends("plugin:@angular-eslint/template/recommended").map(config => ({
    ...config,
    files: ["**/*.html"],
})), {
    files: ["**/*.html"],
    rules: {},
}];
