// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

/// <reference types="cypress" />

declare namespace Cypress {
  interface Chainable {
    /**
     * Custom command to login
     * @example cy.login('email@example.com', 'password')
     */
    // login(email: string, password: string): Chainable<void>
  }
}

// Example custom command
// Cypress.Commands.add('login', (email: string, password: string) => {
//   cy.visit('/login');
//   cy.get('input[formControlName="email"]').type(email);
//   cy.get('input[formControlName="password"]').type(password);
//   cy.get('button[type="submit"]').click();
// });
