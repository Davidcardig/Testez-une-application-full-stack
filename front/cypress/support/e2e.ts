// ***********************************************************
// This example support/e2e.ts is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands';

// Import code coverage support
import '@cypress/code-coverage/support';

// Supprimer les erreurs Chromium non critiques de la console
Cypress.on('uncaught:exception', (err) => {
  // Ignorer les erreurs display_layout de Chromium
  if (err.message.includes('display_layout')) {
    return false;
  }
  // Laisser passer les autres erreurs
  return true;
});

// Supprimer les warnings de la console du navigateur
Cypress.on('window:before:load', (win) => {
  const originalError = win.console.error;
  win.console.error = function (...args) {
    const errorMessage = args.join(' ');
    // Filtrer les erreurs Chromium non critiques
    if (
      errorMessage.includes('display_layout') ||
      errorMessage.includes('PlacementList must be sorted')
    ) {
      return;
    }
    originalError.apply(win.console, args);
  };
});

// Alternatively you can use CommonJS syntax:
// require('./commands')
