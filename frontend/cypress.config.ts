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
      cypress_high_resolution(on, config);
      cypress_mochawesome_reporter(on);
      config.expose = { ...config.expose, ...pickPublicConfig(config.env) };
      return config;
    },
    baseUrl: 'http://localhost:4200',
    scrollBehavior: 'center',
  },
});
