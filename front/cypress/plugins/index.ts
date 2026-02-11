/**
 * @type {Cypress.PluginConfig}
 */
export default (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions) => {
  // Activer la couverture de code
  require('@cypress/code-coverage/task')(on, config);

  // `on` is used to hook into various events Cypress emits
  // `config` is the resolved Cypress config
  return config;
};
