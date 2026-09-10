import { defineConfig } from 'cypress';
import cypress_failed_log from 'cypress-failed-log/src/failed';
import cypress_high_resolution from 'cypress-high-resolution';
import cypress_mochawesome_reporter from 'cypress-mochawesome-reporter/plugin';

/**
 * Non-secret variables that specs may read synchronously via Cypress.expose().
 * Everything else stays in the Node process and must be read with cy.env().
 */
const PUBLIC_CONFIG_KEYS = ['API_URL', 'API_URL_UNAUTHORIZED'] as const;

/**
 * Re-enables the software renderer for WebGL.
 *
 * The SePoDi map is drawn by MapLibre into a WebGL canvas. A CI container has no GPU, so WebGL can
 * only be served by SwiftShader. Chromium removed the automatic SwiftShader fallback in M139:
 * `getContext('webgl2')` returns `null` instead. Cypress 15 shipped Chromium 138 and was still
 * covered by the fallback, Cypress 16 ships Chromium 146 and is not — the map then stays empty,
 * the service point form misbehaves and every SePoDi test after it fails on a leftover dialog.
 * Locally the flag is a no-op because a real GPU is present.
 *
 * This only reaches a browser Cypress launches as its own process. Cypress runs Electron inside the
 * Cypress process itself, where Chromium switches can no longer be appended, which is why the suite
 * is pinned to Chrome (`--browser chrome` in the npm scripts).
 */
const SOFTWARE_WEBGL_ARG = '--enable-unsafe-swiftshader';

type BrowserLaunchHandler = (
  browser: Cypress.Browser,
  launchOptions: Cypress.BeforeBrowserLaunchOptions
) => Cypress.BeforeBrowserLaunchOptions | void | Promise<Cypress.BeforeBrowserLaunchOptions | void>;

/**
 * Bridges the existing CYPRESS_ env-var and cypress.env.json hand-over into `expose`, so the
 * contract towards CI stays unchanged while specs stop using Cypress.env().
 */
function pickPublicConfig(env: Record<string, unknown>): Record<string, unknown> {
  return Object.fromEntries(
    PUBLIC_CONFIG_KEYS.filter((key) => env[key] !== undefined).map((key) => [key, env[key]])
  );
}

export default defineConfig({
  videosFolder: 'cypress/test-results/videos',
  screenshotsFolder: 'cypress/test-results/screenshots',
  fixturesFolder: 'cypress/fixtures',
  viewportWidth: 1920,
  viewportHeight: 1080,
  videoCompression: false,
  defaultCommandTimeout: 10000,
  pageLoadTimeout: 60000,
  requestTimeout: 60000,
  responseTimeout: 60000,
  retries: {
    runMode: 2,
    openMode: 0,
  },
  video: true,
  reporter: 'cypress-mochawesome-reporter',
  reporterOptions: {
    reportDir: 'cypress/test-results/reports',
    charts: true,
    reportPageTitle: 'Atlas E2E Tests',
    embeddedScreenshots: true,
    inlineAssets: true,
    saveAllAttempts: true,
    debug: true,
    saveJson: true,
  },
  // cypress-high-resolution 2.x reads `resolution` from `expose`, no longer from `env`.
  expose: {
    resolution: 'high',
  },
  e2e: {
    async setupNodeEvents(on, config) {
      on('task', {
        failed: cypress_failed_log(),
        log: (message: string) => {
          console.log(message);
          return null;
        },
      });

      // Cypress accepts a single `before:browser:launch` handler and cypress-high-resolution
      // registers one itself, so the handlers are collected and run from one registration.
      const browserLaunchHandlers: BrowserLaunchHandler[] = [];
      const collectingOn = ((event: string, handler: unknown) => {
        if (event === 'before:browser:launch') {
          browserLaunchHandlers.push(handler as BrowserLaunchHandler);
          return undefined;
        }
        return (on as (event: string, handler: unknown) => unknown)(event, handler);
      }) as typeof on;

      cypress_high_resolution(collectingOn, config);
      cypress_mochawesome_reporter(collectingOn);

      on('before:browser:launch', async (browser, launchOptions) => {
        let options = launchOptions;
        for (const handler of browserLaunchHandlers) {
          options = (await handler(browser, options)) ?? options;
        }
        // Electron ignores the switch (it warns about it), Firefox does not know it at all.
        if (browser.family === 'chromium' && browser.name !== 'electron' && !options.args.includes(SOFTWARE_WEBGL_ARG)) {
          options.args.push(SOFTWARE_WEBGL_ARG);
        }
        return options;
      });

      config.expose = { ...config.expose, ...pickPublicConfig(config.env) };
      return config;
    },
    baseUrl: 'http://localhost:4200',
    scrollBehavior: 'center',
  },
});
