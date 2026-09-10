import { defineConfig } from 'cypress';
import cypress_failed_log from 'cypress-failed-log/src/failed';
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
 * Cypress process itself, where Chromium switches can no longer be appended and `launchOptions.args`
 * is ignored. The suite therefore runs on a real Chromium browser: the CI image provides `chromium`,
 * which is what `npm run cypress:run` asks for. `cypress open` leaves the choice to Cypress' browser
 * picker, so a developer without Chromium can simply pick the locally installed Chrome.
 */
const SOFTWARE_WEBGL_ARG = '--enable-unsafe-swiftshader';

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
  e2e: {
    async setupNodeEvents(on, config) {
      on('task', {
        failed: cypress_failed_log(),
        log: (message: string) => {
          console.log(message);
          return null;
        },
      });

      cypress_mochawesome_reporter(on);

      on('before:browser:launch', (browser, launchOptions) => {
        // Electron ignores extra switches (it warns about them), Firefox does not know them at all.
        if (browser.family === 'chromium' && browser.name !== 'electron') {
          launchOptions.args.push(
            SOFTWARE_WEBGL_ARG,
            // Without this the window is 1280x720 and screenshots and video are downscaled.
            `--window-size=${config.viewportWidth},${config.viewportHeight}`,
            '--force-device-scale-factor=1'
          );
        }
        return launchOptions;
      });

      config.expose = { ...config.expose, ...pickPublicConfig(config.env) };
      return config;
    },
    baseUrl: 'http://localhost:4200',
    scrollBehavior: 'center',
  },
});
