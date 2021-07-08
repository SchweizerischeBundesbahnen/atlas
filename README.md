# ESTA Cloud Angular Template

See our changes in the [CHANGELOG.md](./CHANGELOG.md)

[Source Repo](https://code.sbb.ch/projects/KD_ESTA_BLUEPRINTS/repos/esta-cloud-angular/browse)
[ESTA Documentation](https://confluence.sbb.ch/display/CLEW/ESTA-Web)

## IMPORTANT: Steps required to use this Blueprint

This blueprint is intended as an example on how to use/configure an Angular
application inside the SBB. You can also copy the code or fork this repository (IMPORTANT: Disable fork syncing).

If you copy/fork this project, you need to follow these steps:

- Replace the string "esta-cloud-angular" with your project name across this project.
- Replace the string "estasch" with your project key across this project.
- Change the Authentication configuration according to your needs. See OAuth2 below.
  See `environment.ts`, `environment.prod.ts` and `app.modules.ts` for required changes.

### Deployment

To deploy this project, fill out the properties `openshiftProject` and `openshiftJenkinsCredentialsId` in the file `estaCloudPipeline.json`.

- If you don't have an OpenshiftProject, you can easily create one using the new [CLEW Portal](https://self.sbb-cloud.net/tools/openshift/standalone).
- For the JenkinsCredential you find assistance [here](https://self.sbb-cloud.net/tools/jenkins).

## OAuth2

OAuth2 (Authorization Code Flow without secret) against Azure AD has been integrated in this blueprint.
To properly configure it see [ESTA Azure Authentication](https://confluence.sbb.ch/display/CLEW/Azure+AD).
There is an example registration yml in `azure-app-registration.yml`.

If you want to automate the Azure AD app registration, you will need to sign up to the [Azure AD API](https://developer.sbb.ch/apis/azure_ad_api/information).
You can then add it to the stages section of the `estaCloudPipeline.json`.
See `Azure AD AppRegistration configuration structure` in (https://confluence.sbb.ch/display/CLEW/Esta+Cloud+Pipeline+-+Parameter+Documentation).

After completing the registration you have to adjust your Angular environment appropriately in `environment.ts` and `environment.prod.ts`.

## i18n

This project uses [Angular i18n](https://angular.io/guide/i18n) for its internationalization (See src/locales).
It is configured to use XLIFF 2.0.
To translate the app, either edit the XML files directly or start
[angular-t9n](https://www.npmjs.com/package/angular-t9n) by running the command `npm run t9n` and
opening `http://localhost:4300`.

If using the sbb-angular libraries, pre translated files are available and configured to be use in angular.json.
This will however currently cause a duplication warning when building the app. This should be fixed in a future release.

CLEW Documentation: https://confluence.sbb.ch/x/f4pKXg

Sprachdienst SBB: https://sbb.sharepoint.com/sites/intranet-organisation/de/Seiten/kom-mf-sd.aspx

## IE11

IE11 support has been removed from this blueprint. While it is still technically possible to use IE11
with Angular 12, it requires various changes to the configuration.

## Linting

This project uses [angular-eslint](https://github.com/angular-eslint/angular-eslint) for linting purposes,
which is the recommended replacement for tslint and codelyzer. Use the eslint plugin for
[VS Code](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) or for
[IntelliJ](https://www.jetbrains.com/help/idea/eslint.html).

## Prettier

This project is configured with [prettier](https://prettier.io/), which is an opinionated code formatter.
Run it with `npm run format`. It is also configured as a pre-commit git hook, which will be applied to changed files.

## Helm

This project uses [Helm](https://helm.sh/).
For more in depth examples and explanation check out the dedicated [Helm blueprint](https://code.sbb.ch/projects/KD_ESTA_BLUEPRINTS/repos/esta-cloud-pipeline-helm/browse).

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## Questions/Suggestions

If you have questions or suggestions in regard to this repository, feel free to open a [CLEW issue](http://confluence.sbb.ch/display/clew/)
with the Component `ESTA Web`. We appreciate any kind of feedback.
